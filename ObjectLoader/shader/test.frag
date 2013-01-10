#version 150

uniform vec4 ambientLight;
uniform vec4 diffuseLight;
uniform vec4 specularLight;
uniform float shininess;
uniform vec4 lightPosition;
uniform sampler2D tex;

in vec3 pd_v;
in vec3 pd_N;
in vec2 pd_TextureCoord;
in vec4 pd_LightSource;
in vec4 pd_Ambient;
in vec4 pd_Diffuse;
in vec4 pd_Specular;
in float pd_Shininess;

out vec4 outColor;

void main(void) {
	vec3 L = normalize(pd_LightSource.xyz - pd_v);   
	vec3 E = normalize(-pd_v); // we are in Eye Coordinates, so EyePos is (0,0,0)  
	vec3 R = normalize(-reflect(L, pd_N));  

	//calculate Ambient Term:  
	vec4 Iamb = ambientLight;

	//calculate Diffuse Term:  
	vec4 Idiff = diffuseLight * max(dot(pd_N,L), 0.0);
	Idiff = clamp(Idiff, 0.0, 1.0);

	// calculate Specular Term:
	vec4 Ispec = specularLight * pow(max(dot(R, E), 0.0), 0.3 * shininess);
	Ispec = clamp(Ispec, 0.0, 1.0); 

	// write Total Color:  
	outColor = vec4(0.5, 0.5, 0.1, 1.0) + Iamb + Idiff + Ispec;
}