uniform mat4 u_projTrans;
uniform sampler2D u_texture;
uniform sampler2D u_depth_buffer;
uniform sampler2D u_palette_texture;
uniform float u_palette_size;
uniform bool u_useHSL;

varying vec2 v_texCoords;

vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);

vec3 rgbToHSL(vec3 c) {
    vec3 hsl = c;

    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    float t = abs(q.x + (q.w - q.y) / (6.0 * d + e));
    return vec3(t, d / (q.x - e), q.x);


    return hsl;
}

vec4 colorToPalette(vec4 color) {
    if(u_useHSL) {
        color = vec4(rgbToHSL(color.rgb), color.a);
    }
    vec3 closest = texture2D(u_palette_texture, vec2(0,0)).rgb;
    vec3 rgbClosest = closest;
    if(u_useHSL) {
        closest = rgbToHSL(closest);
    }
    float nearestDistance = distance(color.rgb, closest);
    for( float i = 0.0; i <= u_palette_size; i++) {
        vec3 c = texture2D(u_palette_texture, vec2(i / u_palette_size, 0)).rgb;
        vec3 cRGB = c;
        if(u_useHSL) {
                c = rgbToHSL(c);
        }
        float distance = distance(color.rgb, c);
        if(distance < nearestDistance) {
            closest = c;
            rgbClosest = cRGB;
            nearestDistance = distance;
        }
    }
    return vec4(rgbClosest, color.a);
}

void main() {
    vec4 scrCoord = u_projTrans * gl_FragCoord;
    vec2 texCoord = 0.5 + scrCoord.xy * 0.5;
    vec4 color = texture2D(u_texture, texCoord);
    vec4 depth = texture2D(u_depth_buffer, texCoord);
    gl_FragColor = colorToPalette(color);
    gl_FragDepth = depth.r;
}