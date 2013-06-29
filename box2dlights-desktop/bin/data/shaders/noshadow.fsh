#ifdef GL_ES
	precision lowp float;
	#define MED mediump
#else
	#define MED
#endif

varying MED vec2 v_texCoords;
uniform sampler2D u_texture;

void main()
{
	gl_FragColor = texture2D(u_texture, v_texCoords);                     
}