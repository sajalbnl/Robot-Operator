#version 100

attribute vec4 a_Position;
uniform mat4 u_MVPMatrix; // Model-View-Projection matrix

void main() {
    gl_Position = u_MVPMatrix * a_Position;
}