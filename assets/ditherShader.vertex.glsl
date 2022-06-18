attribute vec4 a_position;
attribute vec4 a_color;
attribute vec3 a_normal;

uniform float u_pixelSize;
uniform mat4 u_projTrans;
uniform sampler2D u_texture;
uniform sampler2D u_depth_buffer;

varying vec4 v_position;
varying vec2 v_texCoords;

void main() {
    gl_Position = u_projTrans * a_position;
    v_position = a_position;
}