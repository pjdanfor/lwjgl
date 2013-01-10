#version 150

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec4 lightPosition;

in vec3 pd_v;
in vec3 pd_N;
in vec4 pd_LightSource;

out vec4 outColor;

void main(void) {
	float my_Shininess = 80;
	vec4 my_Ambient = vec4(0.7, 0.7, 0.7, 0.1);
	vec4 my_Diffuse = vec4(0.1, 0.5, 0.8, 1.0);
	vec4 my_Specular = vec4(1.0, 1.0, 1.0, 1.0);

	vec3 L = normalize(pd_LightSource.xyz - pd_v);   
	vec3 E = normalize(-pd_v); // we are in Eye Coordinates, so EyePos is (0,0,0)  
	vec3 R = normalize(-reflect(L, pd_N));  

	//calculate Ambient Term:  
	vec4 Iamb = my_Ambient;

	//calculate Diffuse Term:  
	vec4 Idiff = my_Diffuse * max(dot(pd_N,L), 0.0);
	Idiff = clamp(Idiff, 0.0, 1.0);     

	// calculate Specular Term:
	vec4 Ispec = my_Specular * pow(max(dot(R, E), 0.0), 0.3 * my_Shininess);
	Ispec = clamp(Ispec, 0.0, 1.0); 

	// write Total Color:  
	outColor = vec4(0.6, 0.5, 0.1, 1.0) + Idiff + Ispec;
}