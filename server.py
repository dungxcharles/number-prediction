import torch
from torch import nn
from torchvision import transforms
from PIL import Image
import PIL.ImageOps
import io

from fastapi import FastAPI, File, UploadFile
from fastapi.middleware.cors import CORSMiddleware
import uvicorn

class NeuralNetwork(nn.Module):
    def __init__(self):
        super().__init__()
        self.flatten = nn.Flatten()
        self.linear_relu_stack = nn.Sequential(
            nn.Linear(28*28, 512),
            nn.ReLU(),
            nn.Linear(512, 512),
            nn.ReLU(),
            nn.Linear(512, 10)
        )

    def forward(self, x):
        x = self.flatten(x)
        return self.linear_relu_stack(x)

model = NeuralNetwork()
model.load_state_dict(torch.load("mnist_mlp.params", weights_only=True))
model.eval()

def predict(image_bytes):
    try:
        image = Image.open(io.BytesIO(image_bytes))

        if image.mode != 'RGB':
            image = image.convert('RGB')
        image = PIL.ImageOps.invert(image)

        # Grayscale TRƯỚC khi xử lý bounding box
        gray_image = image.convert('L')

        # Ngưỡng 128
        bw_image = gray_image.point(lambda x: 0 if x < 128 else 255, '1')
        bbox = bw_image.getbbox()

        if bbox:
            gray_image = gray_image.crop(bbox)
            width, height = gray_image.size
            max_dim = max(width, height)

            # Làm việc trên grayscale thay vì RGB
            square_image = Image.new('L', (max_dim, max_dim), 0)
            offset = ((max_dim - width) // 2, (max_dim - height) // 2)
            square_image.paste(gray_image, offset)

            pad = int(max_dim * 0.2)
            image = PIL.ImageOps.expand(square_image, border=pad, fill=0)
        else:
            image = gray_image

        transform = transforms.Compose([
            transforms.Grayscale(num_output_channels=1),  # an toàn nếu chưa phải L
            transforms.Resize((28, 28), interpolation=transforms.InterpolationMode.LANCZOS),
            transforms.ToTensor(),
            transforms.Normalize((0.1307,), (0.3081,))
        ])

        tensor_img = transform(image).unsqueeze(0)

        with torch.no_grad():
            logits = model(tensor_img)
            predicted_class = logits.argmax(1).item()

        return predicted_class

    except Exception as e:
        print(f"Error: {e}")
        return -1

app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.post("/process-image")
async def process_image(image: UploadFile = File(...)):
    print("Receiving request...")

    try:
        image_bytes = await image.read()

        print(f"Processing image ...")

        processing_result = predict(image_bytes)

        return {
            "result": processing_result
        }
    except Exception as e:
        print("An error has occured")
        return {"error": str(e)}

if __name__ == "__main__":
    uvicorn.run("server:app", host="127.0.0.1", port=5000, reload=True)

