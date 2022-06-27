uniform mat4 u_projTrans;
uniform mat4 u_worldTrans;
uniform vec4 u_borderColor;

varying vec4 v_position;

void main() {
    gl_FragColor = vec4(u_borderColor);
}