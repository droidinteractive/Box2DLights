attribute vec4 a_position;
uniform vec2 dir;
uniform float u_fbowidth;
uniform float u_fboheight;
attribute vec2 a_texCoord;
varying vec2 v_texCoords0;
varying vec2 v_texCoords1;
varying vec2 v_texCoords2;
varying vec2 v_texCoords3;
varying vec2 v_texCoords4;

void main()
{
	vec2 further = vec2(3.2307692308 / u_fbowidth, 3.2307692308 / u_fboheight );
	vec2 closer = vec2(1.3846153846 / u_fbowidth, 1.3846153846 / u_fboheight );
	
	vec2 f = further * dir;
	vec2 c = closer * dir;
	v_texCoords0 = a_texCoord - f;
	v_texCoords1 = a_texCoord - c;
	v_texCoords2 = a_texCoord;
	v_texCoords3 = a_texCoord + c;
	v_texCoords4 = a_texCoord + f;
	gl_Position = a_position;
}