import torch
from torch import nn
from torchvision import transforms
from PIL import Image
import sys
import PIL.ImageOps

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

def predict(image_path):
    model = NeuralNetwork()
    model.load_state_dict(torch.load("mnist_mlp.params", weights_only=True))
    model.eval()

    try:
        image = Image.open(image_path)

        if image.mode != 'RGB':
            image = image.convert('RGB')
        image = PIL.ImageOps.invert(image)

        # Grayscale TRƯỚC khi xử lý bounding box
        gray_image = image.convert('L')

        # Ngưỡng 128 thay vì 50
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
            transforms.Normalize((0.1307,), (0.3081,))  # ← quan trọng nhất
        ])

        tensor_img = transform(image).unsqueeze(0)

        with torch.no_grad():
            logits = model(tensor_img)
            predicted_class = logits.argmax(1).item()

        print(predicted_class)

    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    if len(sys.argv) > 1:
        img_path = sys.argv[1]
        predict(img_path)
    else:
        print("Please provide image path")
