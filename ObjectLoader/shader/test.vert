#version 150

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec4 lightPosition;

in vec3 in_Position;
in vec3 in_Normal;
in vec2 in_TextureCoord;
in vec4 in_Ambient;
in vec4 in_Diffuse;
in vec4 in_Specular;
in float in_Shininess;

out vec3 pd_v;
out vec3 pd_N;
out vec4 pd_LightSource;
out vec4 pd_Ambient;
out vec4 pd_Diffuse;
out vec4 pd_Specular;
out float pd_Shininess;

void main(void) {
	mat3 my_NormalMatrix = mat3(transpose(inverse(viewMatrix * modelMatrix)));
	pd_v = vec3(viewMatrix * modelMatrix * vec4(in_Position, 1.0));
	pd_N = normalize(my_NormalMatrix * in_Normal);
	pd_LightSource = viewMatrix * modelMatrix * lightPosition;
	pd_Ambient = in_Ambient;
	pd_Diffuse = in_Diffuse;
	pd_Specular = in_Specular;
	pd_Shininess = in_Shininess;
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(in_Position, 1.0);
}