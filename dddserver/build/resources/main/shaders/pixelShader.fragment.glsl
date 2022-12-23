uniform mat4 u_projTrans;
uniform vec2 u_screenSize;
uniform float u_pixelSize;

varying vec4 v_color;
varying vec4 v_vertex;
varying vec3 v_normal;
varying vec2 v_texCoords;
varying vec4 v_position;

uniform sampler2D u_texture;
uniform sampler2D u_depth_buffer;

vec4 frag(vec4 pos, vec4 scrPos) {

    vec2 tex_offset = 1.0 / u_screenSize;
    vec2 texCoord = 0.5 + scrPos.xy * 0.5;

    float nearestDepth = 1.0;
    vec4 nearestColor = texture2D(u_texture, texCoord);
    vec4 nearestRawDepth = texture2D(u_depth_buffer, texCoord);

    float range = floor(u_pixelSize / 2.0);

    for( float u = -range; u <= range; u++) {
        for ( float v = -range; v <= range; v++) {
            vec2 newCoord = texCoord + vec2(u * tex_offset.x, v * tex_offset.y);
            vec4 neighbour = texture2D(u_texture, newCoord);
            vec4 neighbourDepth = texture2D(u_depth_buffer, newCoord);
            float depth = neighbourDepth.r;
            bool nearer = depth <= nearestDepth;
            nearestColor = nearer ? neighbour : nearestColor;
            nearestDepth = nearer ? depth : nearestDepth;
            nearestRawDepth = nearer ? neighbourDepth : nearestRawDepth;
        }
    }

    return nearestColor;
}

void main()
{
    gl_FragColor = frag(v_position, u_projTrans * gl_FragCoord);
}

