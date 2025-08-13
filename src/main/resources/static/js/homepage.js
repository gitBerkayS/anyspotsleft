function show() {
    const nav = document.querySelector(".hamburger-nav");
    const button = document.querySelector(".hamburger-lines");
    const profilePicWrapper = document.querySelector(".profile-pic-wrapper");

    nav.classList.toggle("active");
    button.classList.toggle("open");

    profilePicWrapper.classList.toggle("active");

    const isOpen = nav.classList.contains("active");
    localStorage.setItem("hamburgerOpen", isOpen);
}

window.addEventListener("DOMContentLoaded", () => {
    const nav = document.querySelector(".hamburger-nav");
    const button = document.querySelector(".hamburger-lines");
    const profilePicWrapper = document.querySelector(".profile-pic-wrapper");
    const wasOpen = localStorage.getItem("hamburgerOpen") === "true";

    if (wasOpen) {
        nav.classList.add("active");
        button.classList.add("open");
    }

    if (wasOpen) {
        profilePicWrapper.classList.add("active");
    }
});
