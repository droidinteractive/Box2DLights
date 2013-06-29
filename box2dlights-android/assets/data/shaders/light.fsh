#ifdef GL_ES
	precision mediump float;
	#define MED mediump
#else
	#define MED
#endif

uniform int u_gamma;
varying vec4 v_color;
void main()
{
	if(u_gamma == 0) 
		gl_FragColor = v_color;
	else
		gl_FragColor = sqrt(v_color);
}