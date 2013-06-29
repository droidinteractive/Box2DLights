attribute vec4 vertex_positions;
attribute vec4 quad_colors;
attribute float s;
uniform mat4 u_projTrans;
varying vec4 v_color;

void main()
{
	v_color = s * quad_colors;            
	gl_Position =  u_projTrans * vertex_positions;
}