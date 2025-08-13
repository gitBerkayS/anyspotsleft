// @ts-check
/// <reference types="google.maps" />
/* global google, listings */

let map;
let openModalTimer = null;

function zoomThenOpen(position, listing) {
    map.panTo(position);
    map.setZoom(14);


    if (openModalTimer) clearTimeout(openModalTimer);

    google.maps.event.addListenerOnce(map, 'idle', () => {
        openModalTimer = setTimeout(() => openListingModal(listing), 1000);
    });
}

function getQueryParam(name) {
    const url = new URLSearchParams(window.location.search);
    return url.get(name);
}

function geocode(geocoder, address) {
    return new Promise((resolve, reject) => {
        geocoder.geocode({ address }, (results, status) => {
            if (status === "OK" && results[0]) resolve(results[0].geometry.location);
            else reject({ address, status });
        });
    });
}

function normalizeAddress(listing) {
    if (listing?.fullAddress) return listing.fullAddress;
    const ps = listing?.parkingSpot || {};
    const country = ps.country && ps.country.trim() ? ps.country : "Canada";
    return [ps.address, ps.city, ps.postalCode, country].filter(Boolean).join(", ");
}

async function initMap() {
    const { Map } = await google.maps.importLibrary("maps");
    const { AdvancedMarkerElement } = await google.maps.importLibrary("marker");

    map = new Map(document.getElementById("map"), {
        center: { lat: 49.2827, lng: -123.1207 },
        zoom: 12,
        mapId: "7380b090fca5fb72c7062dbc",
        fullscreenControl: false,
        streetViewControl: false,
        zoomControl: false,
        mapTypeControl: false
    });

    const geocoder = new google.maps.Geocoder();
    const bounds = new google.maps.LatLngBounds();

    const data = (typeof window.listings === "string") ? JSON.parse(window.listings) : window.listings;

    if (Array.isArray(data) && data.length) {
        for (const listing of data) {
            const ps = listing?.parkingSpot || {};
            const address = normalizeAddress(listing);
            if (!address) continue;

            try {
                const position = (ps.lat != null && ps.lng != null)
                    ? { lat: ps.lat, lng: ps.lng }
                    : await geocode(geocoder, address);

                const img = document.createElement("img");
                img.src = "/images/icons/love.svg";
                img.alt = "marker";
                img.style.width = "37px";

                const marker = new google.maps.marker.AdvancedMarkerElement({
                    map,
                    position,
                    content: img,
                    title: address
                });

                marker.addListener('click', () => zoomThenOpen(position, listing));


                bounds.extend(position);
            } catch (e) {
                console.warn("geocode failed:", e.address, e.status);
            }
        }
        if (!bounds.isEmpty()) map.fitBounds(bounds);
    }

    //fallback
    const q = getQueryParam("location");
    if (bounds.isEmpty() && q) {
        geocoder.geocode({ address: q }, (results, status) => {
            if (status === "OK" && results[0]) {
                map.setCenter(results[0].geometry.location);
                map.setZoom(14);
            }
        });
    }

    //fallback
    if (bounds.isEmpty() && !q && navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            pos => {
                const user = { lat: pos.coords.latitude, lng: pos.coords.longitude };
                map.setCenter(user);
                new google.maps.InfoWindow({ position: user, content: "You are here!" }).open(map);
            },
            () => handleLocationError(true, map.getCenter())
        );
    }
}

function handleLocationError(browserHasGeolocation, pos) {
    new google.maps.InfoWindow({
        position: pos,
        content: browserHasGeolocation
            ? "Error: geolocation failed"
            : "Error: browser failed to geolocate"
    }).open(map);
}

window.initMap = initMap;

//load map
(function loadGoogleMapsAPI() {
    const key = document.querySelector('meta[name="mapsApiKey"]').content;
    const s = document.createElement("script");
    s.src = `https://maps.googleapis.com/maps/api/js?key=${encodeURIComponent(key)}&v=weekly&libraries=places`;
    s.async = true;
    s.defer = true;
    s.onload = initMap;
    document.head.appendChild(s);
})();
