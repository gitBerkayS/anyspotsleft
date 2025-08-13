
(() => {
    document.addEventListener("click", (e) => {
        const opener = e.target.closest("[data-open]");
        if (opener) {
            e.preventDefault();
            const id = opener.getAttribute("data-open");
            const modal = document.getElementById(id);
            if (modal) modal.classList.add("open");
            return;
        }

        const closer = e.target.closest("[data-close]");
        if (closer) {
            e.preventDefault();
            const modal = closer.closest(".modal");
            if (modal) modal.classList.remove("open");
            return;
        }
    });

    function wireBackdropClose(m) {
        m.addEventListener("click", (e) => {
            if (e.target === m) m.classList.remove("open");
        });
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => {
            document.querySelectorAll(".modal").forEach(wireBackdropClose);
        });
    } else {
        document.querySelectorAll(".modal").forEach(wireBackdropClose);
    }

    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") {
            document.querySelectorAll(".modal.open").forEach((m) => m.classList.remove("open"));
        }
    });
})();
