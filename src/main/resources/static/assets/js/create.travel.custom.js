document.getElementById('myForm').addEventListener('submit', function (event) {
    // Check if the form has data-prevent-default attribute set to 'false'
    if (this.getAttribute('data-prevent-default') === 'true') {
        alert("something")
    }
});


function uncheckDriverAndPassenger() {
    let allBox = document.getElementById("allBox");
    let driverCheckbox = document.getElementById("driverCheckbox");
    let passengerCheckbox = document.getElementById("passengerCheckbox");

    if (allBox.checked) {
        driverCheckbox.checked = false;
        passengerCheckbox.checked = false;
    }
}

function uncheckAllAndPassenger() {
    let allBox = document.getElementById("allBox");
    let driverCheckbox = document.getElementById("driverCheckbox");
    let passengerCheckbox = document.getElementById("passengerCheckbox");

    if (driverCheckbox.checked) {
        allBox.checked = false;
        passengerCheckbox.checked = false;
    }
}
function uncheckAllAndDriver() {
    let allBox = document.getElementById("allBox");
    let driverCheckbox = document.getElementById("driverCheckbox");
    let passengerCheckbox = document.getElementById("passengerCheckbox");

    if (passengerCheckbox.checked) {
        driverCheckbox.checked = false;
        allBox.checked = false;
    }
}
