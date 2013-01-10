#version 150

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec4 lightPosition;

in vec3 in_Position;
in vec3 in_Normal;
in vec2 in_TextureCoord;

out vec3 pd_v;
out vec3 pd_N;
out vec2 pd_TextureCoord;
out vec4 pd_LightSource;

void main(void) {
	mat3 my_NormalMatrix = mat3(transpose(inverse(viewMatrix * modelMatrix)));
	pd_v = vec3(viewMatrix * modelMatrix * vec4(in_Position, 1.0));
	pd_N = normalize(my_NormalMatrix * in_Normal);
	pd_TextureCoord = in_TextureCoord;
	pd_LightSource = viewMatrix * modelMatrix * lightPosition;
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(in_Position, 1.0);
}