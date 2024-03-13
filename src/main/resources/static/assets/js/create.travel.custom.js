document.getElementById('myForm').addEventListener('submit', function(event) {
    // Check if the form has data-prevent-default attribute set to 'false'
    if (this.getAttribute('data-prevent-default') === 'true') {
        alert("something")
    }
});