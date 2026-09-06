const canvas = document.getElementById("myCanvas");
const clearButton = document.getElementById("clearButton");
const predictButton = document.getElementById("predictButton");
const ctx = canvas.getContext("2d");

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
    ctx.clearRect(0,0,canvas.width, canvas.height);
});

predictButton.addEventListener("click", function(){

});

// Export image
function exportImg(){
    
}