attribute vec4 a_position;
attribute vec4 a_color;
attribute vec3 a_normal;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

varying vec4 v_position;
varying vec4 v_color;

void main() {
    v_position = a_position;
    v_color = a_color;
    gl_Position = u_projViewTrans  * a_position;
}