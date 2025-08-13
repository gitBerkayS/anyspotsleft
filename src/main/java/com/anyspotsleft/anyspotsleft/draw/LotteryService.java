package com.anyspotsleft.anyspotsleft.draw;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

/**
 * Verifiable randomness helper using commit–reveal.
 *  - On draw creation: generate secretSeed (32 bytes), store only commit = sha256(secretSeed || drawId).
 *  - On close: reveal secretSeed; finalize combines seed + drawId + sorted(entryHashes) [+ optional beacon];
 *              produce deterministic PRNG stream -> pick winners without replacement.
 */
public final class LotteryService {

    public static final int SEED_BYTES = 32;

    public static class Draw {
        public UUID drawId;
        public int maxWinners;
        public String seedCommitmentHex; // stored at creation
        public String seedRevealHex;     // stored at finalize
        public String randomnessBeacon;  // optional
        public String status;            // DRAFT, OPEN, CLOSED, FINALIZED
    }

    public static class Entry {
        public UUID userId;
        public String entryHashHex;
        public Entry(UUID userId, String entryHashHex) {
            this.userId = userId;
            this.entryHashHex = entryHashHex;
        }
    }
//
    /*
        Each Listing Should have the following:
            public String seedCommitmentHex; // stored at creation
            public String seedRevealHex;     // stored at finalize
            public String randomnessBeacon;
            byte seed 32


     */

    /** Generate a 32-byte secret seed and its commitment for a new draw. */
    public static Map<String, String> generateSeedCommitment(UUID drawId) {
        byte[] secret = new byte[SEED_BYTES];
        new SecureRandom().nextBytes(secret);
        String secretHex = toHex(secret);
        String commitment = sha256Hex(concat(secret, drawId.toString().getBytes(StandardCharsets.UTF_8)));
        Map<String, String> out = new HashMap<>();
        out.put("seedRevealHex", secretHex);
        out.put("seedCommitmentHex", commitment);
        return out;
    }

    /** Verify that reveal matches the original commitment stored with the draw. */
    public static boolean verifyReveal(UUID drawId, String seedCommitmentHex, String revealHex) {
        byte[] reveal = fromHex(revealHex);
        String recomputed = sha256Hex(concat(reveal, drawId.toString().getBytes(StandardCharsets.UTF_8)));
        return slowEquals(seedCommitmentHex, recomputed);
    }

    /**
     * Finalize a draw deterministically. Returns winners in ranked order (1..N).
     * The caller must:
     *  - ensure the draw is CLOSED (no more entries),
     *  - run within a SERIALIZABLE or REPEATABLE_READ txn and lock the draw & entries,
     *  - persist winners and mark draw FINALIZED atomically.
     */
    public static List<UUID> finalizeWinners(Draw draw, List<Entry> entries) {
        if (!"CLOSED".equals(draw.status)) {
            throw new IllegalStateException("Draw must be CLOSED to finalize");
        }
        if (entries == null || entries.isEmpty()) {
            throw new IllegalStateException("No valid entries");
        }
        if (!verifyReveal(draw.drawId, draw.seedCommitmentHex, draw.seedRevealHex)) {
            throw new IllegalArgumentException("Reveal does not match commitment");
        }

        // Sort entry hashes deterministically
        entries.sort(Comparator.comparing(e -> e.entryHashHex));

        // Build mix input: reveal + drawId + concat(entryHashes) + optional beacon
        ByteBuffer buf = ByteBuffer.allocate(1024 * 1024);
        buf.put(fromHex(draw.seedRevealHex));
        buf.put(draw.drawId.toString().getBytes(StandardCharsets.UTF_8));
        for (Entry e : entries) buf.put(e.entryHashHex.getBytes(StandardCharsets.UTF_8));
        if (draw.randomnessBeacon != null) buf.put(draw.randomnessBeacon.getBytes(StandardCharsets.UTF_8));
        byte[] mix = Arrays.copyOf(buf.array(), buf.position());

        byte[] initial = sha256(mix);

        // PRNG: HMAC-DRBG-ish using counter with SHA-256 (no external key)
        List<UUID> pool = new ArrayList<>();
        for (Entry e : entries) pool.add(e.userId);

        List<UUID> winners = new ArrayList<>();
        long counter = 0;
        RandomIndexStream ris = new RandomIndexStream(initial);
        while (winners.size() < draw.maxWinners && !pool.isEmpty()) {
            int pick = ris.next(pool.size());
            UUID chosen = pool.get(pick);
            winners.add(chosen);
            pool.remove(pick);
            counter++;
        }
        return winners;
    }

    // Helper
    private static class RandomIndexStream {
        private byte[] state;
        private long counter = 0L;

        RandomIndexStream(byte[] seed) {
            this.state = seed;
        }

        int next(int modulo) {
            if (modulo <= 0) throw new IllegalArgumentException("modulo must be > 0");
            byte[] h = sha256(concat(state, longToBytes(counter)));
            counter++;
            long v = bytesToLong(h);
            if (v < 0) v = -v;
            return (int)(v % modulo);
        }
    }

    private static byte[] longToBytes(long x) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(x);
        return bb.array();
    }
    private static long bytesToLong(byte[] b) {
        ByteBuffer bb = ByteBuffer.wrap(b, 0, 8);
        return bb.getLong();
    }
    private static byte[] concat(byte[] a, byte[] b) {
        byte[] out = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }
    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b: bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
    private static byte[] fromHex(String hex) {
        int len = hex.length();
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
        }
        return out;
    }
    private static String sha256Hex(byte[] data) {
        return toHex(sha256(data));
    }
    private static byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static boolean slowEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int res = 0;
        for (int i = 0; i < a.length(); i++) {
            res |= a.charAt(i) ^ b.charAt(i);
        }
        return res == 0;
    }
}