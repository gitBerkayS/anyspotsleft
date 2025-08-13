document.addEventListener("DOMContentLoaded", callback => {
    const form = document.getElementById("searchForm");
    const input = document.getElementById("searchInput");
    const autocomplete = new google.maps.places.Autocomplete(input);
    const currentLocationBtn = document.getElementById("currentLocationBtn");

    if (currentLocationBtn) {
        currentLocationBtn.addEventListener("click", (e) => {
            e.preventDefault();

            if (!navigator.geolocation) {
                alert("Geolocation is not supported by your browser.");
                return;
            }

            navigator.geolocation.getCurrentPosition((position) => {
                const { latitude, longitude } = position.coords;
                const latlng = { lat: latitude, lng: longitude };
                const geocoder = new google.maps.Geocoder();

                geocoder.geocode({ location: latlng }, (results, status) => {
                    if (status === "OK" && results[0]) {
                        const addressComponents = results[0].address_components;
                        let city = null;
                        let postalCode = null;

                        for (const component of addressComponents) {
                            const types = component.types;
                            if (types.includes("locality")) city = component.long_name;
                            if (types.includes("postal_code")) postalCode = component.long_name;
                        }

                        const locationText = city || postalCode || results[0].formatted_address;
                        input.value = locationText;
                        document.getElementById("hiddenLocationInput").value = locationText;


                        document.getElementById("realSubmitBtn").click();
                    } else {
                        alert("can't get get your location.");
                    }
                });
            }, (err) => {
                console.error(err);
                alert("can't get your location.");
            });
        });
    }




    autocomplete.addListener("place_changed", () => {
        const place = autocomplete.getPlace();
        const types = place.types;

        if (types.includes("postal_code") || types.includes("locality")) {
            if (place.geometry && place.geometry.location) {
                const location = place.geometry.location;
                if (typeof map !== "undefined") {
                    map.setCenter(location);
                    map.setZoom(14);
                }
            }
        } else {
            alert("Please enter a city or postal code");
        }
    });


    if (!form || !input) return;

    input.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            e.preventDefault(); // prevent default submit

            if (input.value.trim() !== "") {
                form.submit();
            }
        }
    });
});