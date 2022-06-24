uniform mat4 u_projTrans;
uniform sampler2D u_texture;
uniform sampler2D u_depth_buffer;
uniform sampler2D u_rgb_palette_texture;
uniform sampler2D u_hsl_palette_texture;
uniform float u_palette_size;
uniform bool u_useHSL;

varying vec2 v_texCoords;

vec4 colorToPalette(vec4 color) {
    vec2 scrPos = gl_FragCoord.xy;
    vec4 rgb = color;
    vec3 closest = texture2D(u_rgb_palette_texture, vec2(0,0)).rgb;
    vec3 secondClosest = texture2D(u_rgb_palette_texture, vec2(0,0)).rgb;
    float closestDistance = 10000.0;
    float secondClosestDistance = 10000.0;
    vec3 c = closest;
    for( float i = 0.0; i <= u_palette_size; i++) {
        vec3 paletteColor = texture2D(u_rgb_palette_texture, vec2(i / u_palette_size, 0)).rgb;
        float dist = distance(color.rgb, paletteColor);
        if(dist < closestDistance) {
            secondClosestDistance = closestDistance;
            secondClosest = closest;
            closest = paletteColor;
            closestDistance = dist;
        }
        else if (dist < secondClosestDistance) {
            secondClosest = paletteColor;
            secondClosestDistance = dist;
        }
    }
    c = closest;
    if(secondClosestDistance - closestDistance < 0.04) {
        if((mod(floor(scrPos.y), 2.0) == 1.0 && mod(floor(scrPos.x), 2.0) == 1.0) || (mod(floor(scrPos.y), 2.0) == 0.0 && mod(floor(scrPos.x), 2.0) == 0.0)) {
            c = secondClosest;
        }
    }
    else if (secondClosestDistance - closestDistance < 0.08) {
        if((mod(floor(scrPos.y), 4.0) == 2.0 && mod(floor(scrPos.x), 4.0) == 2.0) || (mod(floor(scrPos.y), 4.0) == 0.0 && mod(floor(scrPos.x), 4.0) == 0.0)) {
                    c = secondClosest;
        }
    }
    return vec4(c, color.a);
}

void main() {
    vec4 scrCoord = u_projTrans * gl_FragCoord;
    vec2 texCoord = 0.5 + scrCoord.xy * 0.5;
    vec4 color = texture2D(u_texture, texCoord);
    vec4 depth = texture2D(u_depth_buffer, texCoord);
    gl_FragColor = colorToPalette(color);
    gl_FragDepth = depth.r;
}