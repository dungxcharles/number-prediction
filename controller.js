const canvas = document.getElementById("myCanvas");
const clearButton = document.getElementById("clearButton");
const predictButton = document.getElementById("predictButton");
const ctx = canvas.getContext("2d");
ctx.fillStyle = "#ffffff";

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
    ctx.fillStyle = "#ffffff";
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

// predictButton.addEventListener("click", async function(){
//     const dataURL = canvas.toDataURL("image/png");

//     try{
//         const request = await fetch("http://127.0.0.1:5000/process-image",{
//             method: "POST",
//             headers: {
//                 "Content-Type": "application/json"
//             },
//             body: JSON.stringify({ image: dataURL})
//         });

//         const response = await request.json();
//         console.log("Prediction result:\n" + response);
//     }catch (err){
//         console.error("Error occurs: " + err);
//     }
// });

// // Export image
// function exportImg(filename = 'image.png'){
//     const dataURL = canvas.toDataURL('image/png');

//     const link = document.createElement('a');
//     link.download = filename;
//     link.href = dataURL;

//     link.click();
//     link.remove();
//     console.log(`Image is downloaded successfully`);
// }