#version 150

uniform sampler2D tex;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 outColor;

void main() {
	outColor = texture(tex, pass_TextureCoord);
}