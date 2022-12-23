attribute vec4 a_position;
attribute vec4 a_color;
attribute vec3 a_normal;

uniform mat4 u_projTrans;
uniform mat4 u_worldTrans;
uniform vec4 u_borderColor;

varying vec4 v_position;

void main() {
    gl_Position = u_projTrans * u_worldTrans * a_position;
    v_position = a_position;
}