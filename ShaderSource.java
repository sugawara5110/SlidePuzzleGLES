
package com.SlidePuzzle;

public class ShaderSource {

        //バーテックスシェーダ
        public static final String vertexShader =
                  "#version 300 es             \n"
                + "uniform mat4 u_MVPMatrix[128];\n"
                + "in vec4 a_Position;  \n"
				+ "in vec4 a_Uv;        \n"

				+ "out vec2 v_Uv;          \n"
				
                + "void main()                 \n"
                + "{                           \n"
				+ "   v_Uv = a_Uv.xy;          \n"
                + "   gl_Position = u_MVPMatrix[int(a_Uv.z)] * a_Position;\n"
                + "}                            \n";

        //フラグメントシェーダ
        public static final String fragmentShader =
                  "#version 300 es                \n"
                + "precision mediump float;       \n"
				+ "uniform sampler2D texture0;  \n"

				+ "in vec2 v_Uv;           \n"

				+ "out vec4 outColor;      \n"

                + "void main()                  \n"
                + "{                            \n"
                + "   outColor = texture(texture0, v_Uv);\n"
                + "}                           \n";

		//バーテックスシェーダ(動画)
		public static final String vertexShaderMov =
		         "#version 300 es              \n"
               + "uniform mat4 u_MVPMatrix[128];\n" 
               + "uniform mat4 u_STMatrix;   \n" 

               + "in vec4 a_Position; \n" 
               + "in vec4 a_Uv;       \n" 

			   + "out vec4 v_Color;      \n"
               + "out vec2 v_Uv;         \n" 

               + "void main() {            \n" 
               + "  gl_Position = u_MVPMatrix[int(a_Uv.z)] * a_Position;\n" 
			   + "  vec4 uv = vec4(1.0 - a_Uv.x, 1.0 - a_Uv.y, 1.0, 1.0);\n"
               + "  v_Uv = (u_STMatrix * uv).xy;\n" 
               + "}\n";

		//フラグメントシェーダ(動画)
        public static final String fragmentShaderMov =
                  "#version 300 es              \n"
                + "#extension GL_OES_EGL_image_external_essl3 : require\n" 
                + "precision mediump float;       \n" 

                + "in vec2 v_Uv;             \n" 
                + "uniform samplerExternalOES s_Texture;\n" 

				+ "out vec4 outColor;      \n"

                + "void main() {                  \n" 
                + "  outColor = texture(s_Texture, v_Uv);\n" 
                + "}\n";

		 //バーテックスシェーダ(back)
        public static final String vertexShaderBk =
                  "#version 300 es              \n"
                + "uniform mat4 u_MVPMatrix;   \n"
                + "in vec4 a_Position;  \n"
				+ "in vec2 a_Uv;        \n"

				+ "out vec2 v_Uv;          \n"

                + "void main()                 \n"
                + "{                           \n"
				+ "   v_Uv = a_Uv;             \n"
                + "   gl_Position = u_MVPMatrix * a_Position;\n"
                + "}                           \n";

        //フラグメントシェーダ(back)
        public static final String fragmentShaderBk =
                  "#version 300 es              \n"
                + "precision mediump float;       \n"
				+ "uniform sampler2D texture0;  \n"

				+ "in vec2 v_Uv;           \n"

				+ "out vec4 outColor;      \n"

                + "void main()                  \n"
                + "{                            \n"
                + "   outColor = texture(texture0, v_Uv);\n"
                + "}                           \n";

				 //バーテックスシェーダ(2D)
        public static final String vertexShader2D =
                  "#version 300 es              \n"
                + "uniform vec2 u_Transform; \n"
				+ "uniform vec4 u_UvWH; \n"

                + "in vec2 a_Position;  \n"
				+ "in vec2 a_Uv;  \n"

				+ "out vec2 v_Uv;          \n"

                + "void main()                 \n"
                + "{                           \n"
				+ "   v_Uv.x = a_Uv.x + u_UvWH.x;\n"
				+ "   v_Uv.y = a_Uv.y + u_UvWH.y;\n"
                + "   gl_Position.x = (a_Position.x + u_Transform.x) / u_UvWH.z * 2.0 - 1.0;\n"
				+ "   gl_Position.y = (a_Position.y + u_Transform.y) / u_UvWH.w * 2.0 - 1.0;\n"
				+ "   gl_Position.z = 0.0;\n"
				+ "   gl_Position.w = 1.0;\n"
                + "}                           \n";

        //フラグメントシェーダ(2D)
        public static final String fragmentShader2D =
                  "#version 300 es              \n"
                + "precision mediump float;       \n"
				+ "uniform sampler2D texture0;  \n"

				+ "in vec2 v_Uv;           \n"

				+ "out vec4 outColor;      \n"

                + "void main()                  \n"
                + "{                            \n"
                + "   outColor = texture(texture0, v_Uv);\n"
                + "}                           \n";
}
