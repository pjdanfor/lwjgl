#version 150

uniform sampler2D tex;

in vec4 pass_Normal;
in vec2 pass_TextureCoord;

out vec4 outColor;

void main() {
	outColor = vec4(0.74, 0.686, 0.71, 1.0);
}