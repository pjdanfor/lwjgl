#version 150

uniform sampler2D tex;

in vec4 pass_Normal;
in vec2 pass_TextureCoord;

out vec4 outColor;

void main() {
	outColor = vec4(1.0, 0.4, 0.2, 1.0);
}