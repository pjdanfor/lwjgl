#version 120

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec3 in_Position;
in vec3 in_Normal;
in vec2 in_TextureCoord;

varying vec3 frag_Position, frag_Normal;

void main() {
	vec4 eye_Position = viewMatrix * modelMatrix * vec4(in_Position, 1.0);
	gl_Position = projectionMatrix * eye_Position;
	frag_Position = eye_Position.xyz;
	frag_Normal = (viewMatrix * modelMatrix * vec4(in_Normal, 0.0)).xyz;
}