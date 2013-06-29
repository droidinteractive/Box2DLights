#ifdef GL_ES
	precision mediump float;
	#define MED mediump
#else                          
	#define MED
#endif

varying MED vec2 v_texCoords;
uniform sampler2D u_texture;
uniform  vec4 ambient;

void main()
{
	gl_FragColor.rgb = (ambient.rgb + texture2D(u_texture, v_texCoords).rgb);
}