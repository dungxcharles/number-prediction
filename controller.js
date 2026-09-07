const canvas = document.getElementById("myCanvas");
const clearButton = document.getElementById("clearButton");
const predictButton = document.getElementById("predictButton");
const ctx = canvas.getContext("2d");
ctx.fillStyle = "#ffffff";
ctx.fillRect(0,0,canvas.width, canvas.height);

let isDragging = false;

canvas.addEventListener("mousedown", function(){
    isDragging = true;
});

document.addEventListener("mouseup", function(){
    isDragging = false;
});

canvas.addEventListener("mousemove", function (event){
    if (isDragging){
        const rect = canvas.getBoundingClientRect();

        const scaleX = canvas.width / rect.width;
        const scaleY = canvas.height / rect.height;

        const x = (event.clientX - rect.left) * scaleX;
        const y = (event.clientY - rect.top) * scaleY;



        console.log(`You click (${x},${y})`);

        ctx.beginPath();
        ctx.arc(x,y,8,0,Math.PI * 2);
        ctx.fillStyle = "#000000";
        ctx.fill();
    }
}); 

clearButton.addEventListener("click", function (){
    ctx.fillStyle = "#ffffff";
    ctx.fillRect(0,0,canvas.width, canvas.height);
});

predictButton.addEventListener("click", function(){
    canvas.toBlob(async function(blob){
        const formData = new FormData();
        formData.append("image", blob, "drawing.png");

        try {
            const response = await fetch("http://127.0.0.1:5000/process-image", {
                method: "POST",
                body: formData // No headers needed, the browser does it automatically
            });

            const result = await response.json();
            console.log("Prediction result:", result);
        } catch (err) {
            console.error("Error occurs: " + err);
        }
        
    }, "image/png");
});
