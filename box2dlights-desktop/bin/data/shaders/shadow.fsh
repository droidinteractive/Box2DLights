#ifdef GL_ES
	precision mediump float;
	#define MED mediump
#else
	#define MED
#endif

varying MED vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec4 ambient;
void main()
{
	vec4 c = texture2D(u_texture, v_texCoords);
	gl_FragColor.rgb = c.rgb * c.a + ambient.rgb;
	gl_FragColor.a = ambient.a - c.a;                               
}