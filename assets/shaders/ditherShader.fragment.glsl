#define rounding 0.999

uniform float u_pixelSize;
uniform mat4 u_projTrans;
uniform sampler2D u_texture;
uniform sampler2D u_depth_buffer;

varying vec2 v_texCoords;

float pixelate(float alpha_in) {
    vec2 posCS = gl_FragCoord.xy;
    alpha_in = clamp(floor(alpha_in + 0.5), 0.0, 1.0);
    float xfactor = step(mod(abs(floor(posCS.x)), u_pixelSize), rounding);
    float yfactor = step(mod(abs(floor(posCS.y - u_pixelSize)), u_pixelSize), rounding);
    float alpha_out = alpha_in * xfactor * yfactor;
    return alpha_out;
}

void main() {
    vec4 scrCoord = u_projTrans * gl_FragCoord;
    vec2 texCoord = 0.5 + scrCoord.xy * 0.5;
    vec4 color = texture2D(u_texture, texCoord);
    vec4 depth = texture2D(u_depth_buffer, texCoord);
    float alpha = pixelate(color.a);
    gl_FragColor = color;
    gl_FragDepth = depth.r;
    if(alpha == 0.0) {
        discard;
    }
}