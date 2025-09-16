package com.fernan2529.Series;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.fernan2529.R;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;

import java.util.Random;

public class series3 extends AppCompatActivity {
    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;

    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series3);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio); // Botón "aleatorio"

        // Nombres para el Spinner
        final String[] videoNames = {
                "Seleccione el Capitulo",
                "T1-01 Piloto",
                "T1-02 Quieto, Lucifer. Buen diablo.",
                "T1-03 El presunto Príncipe de las Tinieblas",
                "T1-04 Cosas de hombres",
                "T1-05 Lindos zapatos",
                "T1-06 Hijo favorito",
                "T1-07 Compañero alado",
                "T1-08 ¿Et tu, doctor?",
                "T1-09 Un padre entra a un bar",
                "T1-10 Papi",
                "T1-11 San Lucifer",
                "T1-12 #EquipoLucifer",
                "T1-13 Regrésame al Infierno",

                "T2-01 Todo resulta bien para Lucifer",
                "T2-02 Engaña engañadora, dijo la gran zorra infame",
                "T2-03 Devorador de pecados",
                "T2-04 Las señoritas se van",
                "T2-05 El Exterminador",
                "T2-06 Monstruo",
                "T2-07 Mi adorada hija",
                "T2-08 Al filo de la navaja",
                "T2-09 Rompehogares",
                "T2-10 Maldito criminal",
                "T2-11 La azafata interrumpiendo",
                "T2-12 Las agarraderas del amor",
                "T2-13 Un buen día para morir",
                "T2-14 Dulce Estrella de la Mañana",
                "T2-15 Parásito abominable",
                "T2-16 Dios Johnson",
                "T2-17 Simpatía por la Diosa",
                "T2-18 El bueno, el malo y el crujiente",

                "T3-01 Han vuelto, ¿cierto?",
                "T3-02 El que tiene la pequeña zanahoria",
                "T3-03 Señor y señora Mazikeen Smith",
                "T3-04 ¿Qué haría Lucifer?",
                "T3-05 Bienvenida, Charlotte Richards",
                "T3-06 Las Vegas con un poco de dulce",
                "T3-07 Extraoficialmente",
                "T3-08 Chloe se hace Lucifer",
                "T3-09 El hombre del pecado",
                "T3-10 El lugar del pecado",
                "T3-11 ¿Ciudad de ángeles?",
                "T3-12 Todo por ella",
                "T3-13 Hasta que la muerte nos parta",
                "T3-14 La responsabilidad de mi hermano",
                "T3-15 Tonterías de preparatoria",
                "T3-16 Conejillo de indias del infierno",
                "T3-17 ¡Dejemos que la boba cante!",
                "T3-18 El último corazón roto",
                "T3-19 Naranja es la nueva Maze",
                "T3-20 El ángel de San Bernardino",
                "T3-21 Lo que Pierce haga lo puedo mejorar",
                "T3-22 Manos a la obra, Decker",
                "T3-23 Decker por excelencia",
                "T3-24 Un diablo de palabra",
                "T3-25 ¡Bu! Normal",
                "T3-26 Érase una vez",

                "T4-01 Todo está bien",
                "T4-02 Alguien ha estado leyendo el 'Infierno' de Dante",
                "T4-03 Oh, de poca fe, Padre",
                "T4-04 Todo sobre Eva",
                "T4-05 Muerto erecto",
                "T4-06 Orgía a trabajar",
                "T4-07 El diablo es como el diablo hace",
                "T4-08 Súper mal novio",
                "T4-09 Salva a Lucifer",
                "T4-10 ¿Quién es el nuevo rey del infierno?",

                "T5-01 Un pobre diablo",
                "T5-02 ¡Lucifer! ¡Lucifer! ¡Lucifer!",
                "T5-03 ¡Diablo!",
                "T5-04 El pollo siempre acaba mal",
                "T5-05 El inspector Amenadiel",
                "T5-06 BlueBallz",
                "T5-07 Nuestra magia",
                "T5-08 Alerta de spoiler",
                "T5-09 Cena familiar",
                "T5-10 Karaoke celestial",
                "T5-11 Cara de diablo",
                "T5-12 Daniel Espinoza: desnudo y asustado",
                "T5-13 Un poco de acoso es inofensivo",
                "T5-14 No hay nada eterno",
                "T5-15 ¿Así se termina todo?",
                "T5-16 La posibilidad de un final feliz",

                "T6-01 Aquí nunca cambia nada",
                "T6-02 Un pasado que lo flipas",
                "T6-03 Yaba-da-badajo",
                "T6-04 Pinto-pinto-gorgo-papi",
                "T6-05 La muerte de Lucifer Morningstar",
                "T6-06 A que se te ensucia muchísimo más",
                "T6-07 La boda de Eva y su desalmada gemela",
                "T6-08 Salvar al diablo y salvar el mundo",
                "T6-09 Adiós, Lucifer",
                "T6-10 Compañeros Hasta el Final"
        };

        // URLs correspondientes (mismo índice que videoNames)
        final String[] videoUrls = {
                "",
                "https://kllamrd.org/includes/amazn.php?data=8sEGZbVLXongHVqPZ43LLtmaU4A9VTXDKbtLKIwcWSLcEGg0tLSxFvZwGX20BZFQHEMm6So/szbb9ILtd/+K1AQ2P96PWGxKS81XNNmZvlO/5y74ML3eaeT5fNe3pgPh8RZlL5UbG7zrdgus6gbhsZFV/magnxT/WNWydlMvQnzWKO7t5zqaw7nRqARE9ZMoiEPVaqWhsYIAm/59B1rbJXJ4opJQjGmqrC3gjKrCw57y9/+363p14eflc5hCohT1jn1h7qQ+K5+4WKvntfzofoEEm+K8FQ7zmVp3HMaquc9Au6nn37BC849iB+Ofm21j4a5lm3oCtIRVzUFj0ibs5dnnZIzhD+isZ3VcMbpuhJLwH57HpsvkVPykBQNY/HyI1Psxf8B0GgEnq3gTegKvtA==",
                "https://kllamrd.org/includes/amazn.php?data=+jigUVhX0QOyD1pYJnHl/pElFPrctJJfJqqAs6WPH61uXl9iuLty7hO/4HEw4D+48JkNYPcUo3q6J/t6iYqhp8yX71duklwST1ORGnCJ+Iuf2SGBCQ5/GaZRqxfKmpHLUx7JqtgfffxbV6+JnrvhiGo/LE3C9DIDx+uUk33aePkSkvUIlTtMMeEfv9HFUtsTQPcAnaGksG5HAiGRtc5N5uqshRxfJWhyse7E1/2i/UGH0I2HWVNWSSZv95JR5VjfMvSCCrYe/bIRY9kK5UL2104S8SnUNzUH/M5RqedxLliwDNyL7HqNYjv/JMlo+IpM6DlGBjE5K4DXfdCJh3g8hTOnVbLenvra1D/B9TP0xkZ43nYDJFKQBazIsklSZXpviPafTpFQsv0GA8EuzMLqCQ==",
                "https://kllamrd.org/includes/amazn.php?data=ZBRcfS7qazgKe+RyPCY8AxZtJ55aWJ2LDMo9MOXHqN3U/lL8lfxcW98i6tc5YRkU3/bjFJmdXC1NVvnqyqJQ+6KKMiXcqL+wUFFBwQVWjwy/qC3BmU3ffCzX1i48cOv/RgEqphBe0LDzWbfSMnIepqbAWkza9wm1zzB+5xK3DRitfQL0bdlwrD7WPNjw7e4+V+sn5+t9yAbZbDhMraTrbU65Wg98rKOQMUs0KSP5jJ8nDU3nTLL8CTK5A2tYsBk+gYsrrWf/eW6xvBboPnF+4QFERNVhGkuvhgWbG5YphG0ZSpYCfz2I3MOR7Q5YNuT1La3z8FyZslIUIp6RdZJGBtzOnApoIlrPdR0gk3UT3g9hEAz1XadQXdcMtdaa0AERWwqIpE7/ea3EDdc1wCmPsA==",
                "https://kllamrd.org/includes/amazn.php?data=UKQnV1HrI/ytw0yRna44fluy66CEqBHveYTQS0Sjoc6uJo6J5PvetaxtUK0AhyvsExWued9zeommCagQcNGvxX5VIOblqhme9aaUhHYTst7OAd+gKQYMo5PXIHyv+rWEZi8zhsyhjTddm7z0uVeQAAuO7SDqs0Vk4aDQCmbznherIAGedh6IKDm/11+1PT5ddOU9iYkk+WaTzAZ7zt+VlZ3KweZQnn6DNVCag4xk/Kr1hb4tim7paa/aOtiVcFLLgOZe7a4D1/Oa3lhcA5q13CJXMhUnzAl7ozujdFTGpc9W08H9wjzltCT5ZhzN8hmUtKmDM0Or56veH1scOCzctayefsfB7KKiZN5zQsB/xX06/s7YwY5p/JRK3CIx2ez4a/Y17zvx6e75h6c/uqY9pw==",
                "https://kllamrd.org/includes/amazn.php?data=VNyDsBYMbZxjGRifIc0sfac3HlZvidGPTpxYw7Rif3LdYmO60qbhWorJ0eE0hqVIKsHKJJvpcnb69f/BTH44Yk1otZhVT7kS9HtFmn5A0Ujlrlq5kVVPhiy4VFMzAPqUaGmisH1W6raKainKwQ1jLNxCLRAlGfPdYc1KIMhUKR9Eb8D9f+Z4BnHtl77m13B77DwBh5W7IRkgHK2ayuGCqeovQLmzHpVORLqu6O/ellqldkVRamnBuQxqcpdazfJqxoVti9pjMfFWVfecWyW+OlPSrlf9ZwPUYU/yIooAGAsyrPcz1cSd0WNpJtvYCbZjbUevRpi978wJD72+cf3U8AI32jSLcqz1FbsgcUSpLiRw5yvQcmJhClvvSNaIxhgDlQpultdD83oapvZ3N7tn0A==",
                "https://kllamrd.org/includes/amazn.php?data=mRZaUbWvdrO2xBI8MzH0YSeq43/8tQQo9J7fRPz16Xpd0uE/Om7WlElCrV56hTQm3P0ahfdWO7ANWTk6OgFh+1rX2ob6wm5UpMBecdGO1B2Sx4gwR+nqCtk09WgFaZy1su3LCryZ/86kyEPtp/hImTywruxcow7SeiGjWdbHDo4qe8PtaY58XTnP9teWuV7xckBMC0qVvLmfTZmks+FVFGg3uZS2t+lrR6sKK7HnY9tYEqHKNtDgB8f4/9P2JYlFFHobsjM9y/edFyjPTY0Oyhz4fEkBTIV1gpoE0U2WHI7rcMiDIhX+L4BsOb8eKJ7LogRQmBnC4SYftVEoz8+s3F8jB1vGhh2nNNHRF6W6WI5okpymvRtWvbwh0MrXiCRKexK0mLDWf37PPhwhNKGqPw==",
                "https://kllamrd.org/includes/amazn.php?data=pNdhOXBtb8qlS+ok96L6or1+OFf88nuOdD/hFkKGpgAA+qWJ+FfN+3owkclBupo8cDUnXx7jq3ub9JjRt/Gz0QIz/23bKG2qa+jxP6tUYOhMfMYY+IqCLdfWZ7ctsIMi9yVM92jPCo67vpTFbYfBylaeL6RG6q9HCaoWbmo4esoAK3C9tOReNpFy4IHU4XmoUeGlzBeL6J0HHO+b3vh56u7XxgM0HXG/kKMWbA26w9CX0LogT0z1cRKotUka09RXYUtiRTfqK3Q5hr/rdztrrQqsaqcdBk1yzY3hg22GX2o36Hs2xyiw3EVaU4K+voevgB+f2+rq4BvaDX8kvwBbiFDRqnFptrGfPSplwbkst087KmFzOWyhYDYz9hQPBA8WLdGhqgt+t9d5um8gY/SNng==",
                "https://kllamrd.org/includes/amazn.php?data=lF1aAXhF8yDV8jTDM+3JR+rZz7SXaGmxM4ptbaBn1CiGMQKsuo21xjvXkc+tER5pxvRhZcaik2BcKvBTzNQHD0bjJxIzPf8MzfqNAT4m6eal/S9MLTG4PbY+hTr84+pEA4+RL05TxI/HITF/XhQ+c69piorKsLLxnVFFe/5WDkEmGca5kpd8W/MsGFjC9aI3B7qX35XhAwgIj/XCBpz4ukevwOwwLOSVRHhQr8s285syWqebdQclChB+v+q4jPWwu/5V7kcRxaxVpmsxnF/ycClkrfnZnrT23Hf6wNhDaXP8+i+RYD9IKcIxhZXTNjqhT/ruNJuqFeHh6IEXITob+E2jsm2xbqgv1tmVUE0JmpI5kTPAJA+I7ZENaEmqx0wPaj6uu4C8FzsvE60ibd3cpg==",
                "https://kllamrd.org/includes/amazn.php?data=lNoyNa7ZvFKCZ/IR1XccMr/ciqlI3nKzVp6CEc49usl1KJ1Yex0qTs1xzgS/qNnbTgC5Ll51UnLarmNrV2O2ugoMBhn4AdxMvXoPQYXODhYbwT9mPrr94Y2hXjONCKWmHtlus7ysNaKVzw2JUbH/XCBKzjLFhTUZ+tzkutb3BBpLyeRA42zXERrl1/u8PEaP3eDnxtKZqM1YxHzET3jfTsfdwAiGznIwXltSmo8UbelFBUM6e+Fy/TduRyN8OQ7twYM12t4GyBW2rVMkMEKcqNR0zQ2IMPtNm4PKdE1CXpLwUZTc8zl5qHy2MhYa/I/VDbiglKOaNDSh4rGQKFxuBKQMzYrunCUhzE/5bT3xh4MbS8cQRjYVblK5kJ/4/FAPWTKog24aSoiffCVagtFzqQ==",
                "https://kllamrd.org/includes/amazn.php?data=zmFKFi9nbax/LDkeI+bQZi3O7g4LIvoR57qU/WDA24rMK3UuO+UoKV6hVMvYxaEf5LQWP5cs8u6NaqAVJ8HbwBNQ646C9fspnVpgHrkHfzyVsNWwQ/aCSnSISvVw1wGSq0+dn10T5BgwZdFCTc6H7r9IvhV73aasRPvF3V2C/DUY0EaAkXLcES5auYg31VmHwfV5crTHtS16W0IQ3IUqxevsut4uY64l+RgjVYGmhdxyB3WZUSMZ1xUSeuXaQN2LqvD7Tu7djWKC92OyXRqOxPv1uzDzjWh5fNzEon4+BQt7tg/Ipxzu0p0a2smdv5oRRDyqM0C5jz/8aoWUunsD5jsmcri0DJWoWYpv/AkvCd8I30PVfZ+9/57mCZAyKqghjo0vV1nGtB1lfgPXx5GrZQ==",
                "https://kllamrd.org/includes/amazn.php?data=FJn3rMmVVzjKraO0yE3P7kMPOKc4gZ8OsAvmG0QOh9cB2SYKez7dc7YDbffWDJqzJX0lqJJ6bNzt/WyI2RiN9w7te8lWxBcd/cLTkytRl61h1KVzhTgRhQ1AaPJvKGUUCo6fXmb3gmTSG+LA13jy5eU7GgmThSZi7yosLg45rEBGBd4Y+lv73eM+JHV9crvFaxBmaHMyZQNEBvQN2ztw7h6sRwN95RfnJ+clQZChKYMFhF8JgwuNnM6NEkcnzx9AMh7LBaGiFn0vMCPCMm7DD/qVRVkcDJogDwPwCQ9gsguELkD2fH4vroUX8QpftZks6VNaNbljt/MuwT6an0AR94ayWuR4t3T9JpuJT1njp5JRICwofi9Puo7ySeK03q8J8yqzpDwgPxp4EHfRGP2d+Q==",
                "https://kllamrd.org/includes/amazn.php?data=SQXS8HdUoDI92ur5qoB8VOk3noCKllbp0xWC10w5PQG+2sN5fgyLcVbYR5eWwddp3aaYrAIGdT3O91Te8kP13ejCyXsE2Jq8YyfYCbjcA0TlT41cSYgUME34zbmhEprHtIOQPfvDphVZImGo2oZUeV43Sua1OpoqAekVpKl6s/WW/TU7u7ra5jhxfQs96YeGyxHARsHCjuItfByxi8OhrRSm16oSw91PWIiZtNqFF84t6dFlv3JOfCX9AMTeDRMt803CnQfmXTcNSorp3n7Np4y7Kl8qpL6PtHuex1MX71n5w3YlDlT9MU3fneT52sArPNa3owWv9BFcn+OYMvsoDlDofGHL++H7UkJcNxdQVAvYwvbZGcW5kVujrHjJ9pNwtN5flK252oh/blwHCIkZzw==",
                "https://kllamrd.org/includes/amazn.php?data=Sf7SXh/Co7DSY7SWJkFrO0jHjWocL8XbtLAZ2XXhCL/B+czadrwcTCATHqqyeNaTvP0CKpr6mQJDuzsz5o/yh9ueuPbXGlD+sGXeobH6ZQ+8oSL7KG01fwbluK59/g5j5Etb61FbRJLx2GT7AMWXnI2Yp8E/lZo0/eEa5sKghaBsL04DQJkbBsF6ezPbubtUMAk+oo239VxWbhghTGqVvbLDLE5ckTavM7KarQnbYs77hsc4I8URsVNk0CVI+sPw484zmwS2IbuMt+6BfnBIKqW5p/E0D/4sD1Tw4jFk635V+SQKM8J/XtycPhexEWy8da8Omh8eS+O/0AATBcasyhuFxxLtIs1wkHDN/z1vPsoIFKiN/UlS5tJYXaHZAAhY",

                "https://kllamrd.org/includes/amazn.php?data=xhUlMBSaXb8rQyAgYF01WNdZ+lmc72FTSGG3WneWdIT6WqGgYc3P4MhGsZbo7D6UiziFVNT4OJAiSlb5EnLorYUSPrHZWZEBAlW0+Wt826+3ZIl/4CkudvMpudZXHrdAVdY5bqNJ/6jOaf2iEunEhTIZMVO+WWqajIRVNKR58SzcxuNWiHAbJVuaLN+vAXkQTiIdb6DUAFU9JIoqyChjhDjRU8SDjO9UcICItMvAtQTlkfvPJxSKlmFGMraGb2in2K8IJPl1GRnh0RYRejyogs67NuXS/HCr61y+446zDS6mIcDQ1vSSReWX1ZCP8gFHHA6GUKXmaLyDhmf5FGEVztiKRy48ODOiJ5/ycJMlCqA6sMYB4H1te0MhXyk8v1T4puqch9/TxHqFY8GlDHoukg==",
                "https://kllamrd.org/includes/amazn.php?data=CSAsmrwt34zdfiqgnwfpdoPL3D/YsSZqX9tCY6I7ShoaDvgcb3frSKnxxl2mGhbZzAHyKEehixJvr0WsgXa6moLoCBBkPXTXxZfW5VU6ri060Irxcj62JzGukC/LBIxsxahMRnAUFS6Uay/naX1S75rq/OUiWTjazcSzXzeYiquRse2bcTKmM1qynR6iue/qQHn82eAW/MjmNXc2HlV4yMZhgkvjvSMhIhxlS7Qk94QjOU/SGFLOqTI/km2VAsK73VPf5lOzmBOw4rba3JbCGObreGQgrljOFHHf/KXdHzDL4Ssd9at58fZV69ikNxjdAyUsfVLn1u6/PlNxbiihOSEERlbxZJPXLeYIE0DFDDZaBoeI4kfC3gPEgjgc95mqS53dggn5DrXyNIva9PTFVQ==",
                "https://kllamrd.org/includes/amazn.php?data=QYaRmi3LgQ9+EK77jLlet4u4QqgfRzZtEBrijSqvWDq9nSstD7eHY0PdHhOYtxYHuHbixRzQ9mBMeSRFK+BTR2DQebWrOvLIxxJ455CSwPHd/7G37JjBNx/uj9OU5LEK1qUbJgVmYLBPc4oByVWf3jXXcOT3EEinyPCl/GEziUd9X1pFP0CkECFr19tazOiVnZ0oQxcvDgVfXi9/k8kFH+sHjvd2/YrXVdxkAewAcjU9Dagk1IX58UHTtT6FW0CrJlv0mDBguPe0YuXxKTYpTquveMHCnUB4kqOj/DehxvjYqa2OtJ4yOMZf6dHj5fB5ewfx7KItO2japVtPrGupLdZnRpu0TYFbZqI5SfJs8My90TLTIMspPQknlbIdbPYjr+nCkkvd+RX1m2mIQNqKPw==",
                "https://kllamrd.org/includes/amazn.php?data=onE5G+e8QGrgbzv4jPYMC4V2himcta/1k2OSgphgyLijS/ysL6YtTUzIeksBltMiswQaaPROe8eNHCsBIxhusz8CB0qvAIJtTLsyMJHfMlVg8pXTcBoWlfDqpcuO/SjAOOOrcAfqI6XNawyCil7qEcmSrlom5jcT2etQ1qbSUbZxuTMERP/KQY+hyhJqutySA1y7VpIRvQcbXP2pfhXDFJHzcfDuegi9/YT4BJLjJta++aaJMclToNqCoSreFbD3JVYhFPkRl7NzJYNUv4tcSGY2hI88uwMqHI0gwiQRJ3tBQID34BiDrfaFtbGsyYpOh2pmnLeuHIxbgNn7wJhXOitz0p5b81kumjxqR6F8K2k0IaaU7zAcOZbs2rsoUzBAabPIC2GoliHmq9gnm07HXQ==",
                "https://kllamrd.org/includes/amazn.php?data=dJFYBV6ikBXR6YdK2+vJFg3c0EjUBp4yOItTpvgiSXXycQcqriE6tfJj7OlNe1U09K6QPjKOPeOD+vl8eVue/Aqc33F16j3yS3YhexXDMOf2oFe1u+c/a506hQ4+r0guA5dLbuIt1zDn7fqgieibJ1rNTQvE1ywKkffNjnqztrng+x9OcrdKVRHyi4LuyltnzrmbiBP6v8DQroBOdt/Pp5p9ubOEh1K4gp/Re+i9azP6vkw6sZ8X23p8bYiFcqK5P4lA2SPE8gXK2zCQz8eSD149HB7IROGoMeW+wV43O5lr2lyHhSXiLS27MFih/R6l+bXMVv7b6wNG/2R5ZPUQ+l+eYVI4oSbGcLj5PP7OMS9DkldQy2kBBpadRw726NOocgfx0P8KxiT373DycmTekg==",
                "https://kllamrd.org/includes/amazn.php?data=qi+bumRCsTi76JLmZSWE5maUzkP97pq0q/ppYqPCyCokt2y3UL1d3mbHEwV4J0XHMkhymhmPRjAb3G4pid84F2anOp6GZeegUriuaEW/OWCKhjEHGcR05D2PKICwntzQxCxZxszu/FLKG0fGdt+awA9EvkXr1KGvJcMv5VlXBz9trrCiqecrInXEgZMvdwr0F/U7RlN8sNfba4R33dA/2hG6sF3tm4Uaz5XjLXmzB2/CmSzjycNdAy8/LTA7Hxu1oAXfscDS+/U9sfwIrs97JxaTxjGWVD34l+sCcQfescAHwTRVniLkmmFkhg/sbZq+TAAisRzdUojeWm/B0s+KtlJSC/Cbv3znT1IImrK0jqkOVXYNlbEPyRfdtRqqZ73Imxh0ltgwY2ROdjpj3Y2oYw==",
                "https://kllamrd.org/includes/amazn.php?data=SYohWWzrhc6lNlhx23opPoVsfARHos9GT7LTRU3RquNNRkVWt/yXcw8SuxPeIuf8lo2TYCuYQQzwdhlGShf2FjnBImw1LaoQ4K21rfcNjLi+S51rCFLbx0H8RKSPR3HxAsdOrXzO2EvrT62vqfZb/vspltHY/kfvxitvqDHU4JR4JusRcCPlwloihE2X78soedHjFSfnd5P+OevJ331hBio+a00AjhH9h2qqbIg2Q0PiQfnbLIIMnzr7r3FWvHpV8H+vjAcGSKww11IH+CMSO8r1NbMriYIzAw6Nr/T8KJcKRrjWefz3uZqMgiHySMHezB2SbhGtcB2e8J3X/PwiL9P3S5sbhLTR7qf7pKWGtodgU7OEoVDp1/wffu60AXrJRsAbM9+MTx6kY4D9RwGxlA==",
                "https://kllamrd.org/includes/amazn.php?data=Ui++Sd9JO8p4yNJsyTnCiX2Q5uiHH0krin0CcgjzN9R3TJDCdZaoL8k2NtDZ/Ki9OJONJa/Eau3uTKqi/kEG/KKH2j++OzAYMi76Lcxj/0W1Uf1VSarwLfJdsMa9BVVO8IrlWS0QBXwSoG3vT5cXxv6f/c12I74wJ48K4XcVSc/cTS41xKaGpXKwMTpPY1I85Hip3AT2245BB4VcXFStBRqy4ckOaRTKJusiGFlG2gYDnfbYkNi0FDgL2ale6v7mIQ16d92qZ3ac5qzaslHzjAU9RKtcK3zja6CShac62ECGYBVAJR15nR3rdA9q2NJil0uCvQIWsl2YRtAWb/wcaGmjQQZsdyXw+KWu8COz7X/rJxOh/pBHfSYlXaKj04HanzV0b4XgLCFHiNYGBe7+cA==",
                "https://kllamrd.org/includes/amazn.php?data=z+hlLK4k5uFVGUyN2wvgkYJQ8YbRuP7GYmYLtEuRnQoYLXsvlL/hscC+l3smFLrU3edfKvw9AUEl5LMubu0CHGuvNDSKaL7/cZ51R6KYkrmu7AfAwNFhjYVAWxV0CVidaiVZiMwlcv+Y7245eyoA4h2dz+cnW2A3yOB439ix5jN28C7QXJmhDoyucLckV0famOVS0G5Qk7a4DBTCnK8nFvuYTXgbPyGuBGB7jw98AWrWYNf7JX/9KKO2E0IU/2d7kmqak9QaOEFRyfi7Rylj1zYUbfZVI3ieuiziUnDdFCZAz9JD7mKfR3RAdNNFgJmsWt2YjPsfdzzOlgq3dSrCAh4yyOR3hzI1JzmxeCOF3AzeWr5aH1i8YTzPGdcHlKDHrpwiKF9n/SfNVls37L+B1w==",
                "https://kllamrd.org/includes/amazn.php?data=r8AKgbVFTrp954I6RTjViHITYhF426cZoRpQeHfS26aEC0gbUjFUMTznWzTYknOlfIQe2WR6zXnYvKSPwiIRxHlto4n7MtZeIX6CwggLLcZOmPcIigTErk+AZjdqqjC4A4O5hmsmNuC6xkE1FW/fwjexu8KvnUf5wcS9W6JCDrYPc055nk0xGhGwS2r+5zUhZhOmelef7ZR2NVtZmJ8ekOuR4zmsED7EzZltdFsm3ra2jP03fGHbI+AHdqiLZazNaf/7PNeNSxczMmqZ39uRj1W57gynwUOuNVWWcEadvYMu/uqSEj+VjvI1jt+Yx4yt9by+llJCD0R8pNQk3Rxwm6oOYFNJP/1TWurO5DlzkIxLSI9pYWmBBOHMFg2gYlsY5tIKme5rzptFFRzS+cdbSw==",
                "https://kllamrd.org/includes/amazn.php?data=TDv6LSVo6Te3id49r+P2yyC1AVCUzIGfdieKduaPpdDm8oSYhDYlXMsTBNUkyn9w4USdjkXQ18JvwDMfrw0+cerDZy0AvXEvWLi7vCZ3TmpurgXxJpi1NCZqSntdulhmzXa5LPzuKYpSFQ5+Qc56TxOPgYp+L0+mZ0crMnbjAkYMobayR2RRYeBPnB06Dk6Vl7fBAoGyzlhFrPRIlTBZgCwrA3NMwb/UzyeDdrWTUd77DZ0cECqmp7dnrBNqeI4VBQrYxw7ht3k0JzGZuWCe7ITcH9gmZNZ/YerST0IYUyc76Fb4aYvVwi/DM2DHL8GDAsoZkAF/Uxda7ZLEkFp9ApCahx+VAlrTqna6OXM5GBlvUpBKg/wlsZ9mWlxHBCx3Wzb11eGpB8EMSu6T5palqg==",
                "https://kllamrd.org/includes/amazn.php?data=P1/CzNOsEMESHgrEqodPKevsXG4Qr1B/gcIBsLsn+Srs8DWGXGDn069tgPgWrKLfBRo28qyCPWPLuvNCLp4oqgE3L/72J3U8mL6AWPg4zHO3jxgC6MKEmJudMPXHZRdi4IXpaeeIFT9ipIZu4hW3khlKx+SU3SJhoEulhtIgSkr+IT4UkoiyotVhMubfDGAtlB1hWB6qCuAa9+hzH7UDx4bYugbBze0LIwRtZ70JAzVirAXoUGJn3H/g22LMVxEt7djQY1n5sa4mdn13zGk1ukcYxfFAWblzDF1lVVprNRQ8GUbuhX2mOtZCvJvVePBSopS+YPzKYYVwjY3ZBjHCSLh8btKV0pNfhCjVeX8kelB4tqoFVCJB+Pi9kLlm0pNtQfdqixG9Cih6IsH5+L+CzA==",
                "https://kllamrd.org/includes/amazn.php?data=gFioMe1ac9vGXNnRx94GB4Q+F8dJraALUteZ/VwiHqKpnuvYHcvxf7pXmhmI66J8XMUHy/kv/xgOqmOerQK+NC2XRiSquiYmnojtqe0Ytx4tq84g1yyP+tyKwZYjAW5cVPA5hSql7FrqP7NT4xHMbzH6Smk04imrXpDh3okgmwQmRUkANNJK7kCSczBnA5TVznCZSdP1WxvYrgEQy1E63jGQfp/f+BJ0fG0kDS210VgbKHHMAPIM5Z3cl1+lyeA7AYL2MlErtpc9CED+s6Ncit1wh/b2pFZwMJKlxKvebNoJiTkWeXnMpTLywImDkOQvsS7bnIpLZHwupiWtShCMTX6VsOyhztEDqQv0KX80Xuh5u+4rU6telyxPxHkryEOvxqBWnBUOrPw3+YGKJbEahg==",
                "https://kllamrd.org/includes/amazn.php?data=CIQPu5A4AEG18sCsTEDQJWw5utnayQWxgwS4RqvgMUnHGKb1y0aA20erbyx/DcxiNGF5l5TwankVsSpCnfnr7zP4r2pfVzlDDR8wE2KqysMQmYCe0XMds11WQ+lqJE4RmKtGNu3B2Y9WHmTYffucyYGyIijwGWqhgMYSXcWBwPuui09ejml9U0Mz+EonRJ/m3EdVnaA12RCFNt1E0cyQ5p3dKTAWsnuiHDG3UIcksmcYvwTqqtekIQhCPTFDRMeOiFIJsr3vrqw0w8TAE6EtFk7GJICOuhiegmQ8t8eL0yGPld/A56u2A89RPfPj79f2PxDqsCm0rXpvBC4sbDICsmSj9zxegUGm0lvvlGyh4TZAm+9BDYGVg6FgAayJu48+V6n0DZ63ZxkhuGLuP7QVmA==",
                "https://kllamrd.org/includes/amazn.php?data=BQHIrBRk6tKwj6F4zs6e4fV3aI5TJMihHPZRI6oY/RrdP48Wu+D7SBey6PxDy/G2SZqCoXmN0vWXn9fsf5iMozu51cIkLRAKr1/cJO93S4vWSCnoqinFv77VtrVxC3awsROqUG1+PgZQ7rshdWe4pgSkqKoWFObhg05OuEso+k9aV3U7uSoXZLotb5UhZ3+d1m8NQ6FMTmrIdjzBuRN57Ztf7oHW94IRVsFDPmG5sjOXfUKgaTYbeEFOrADvhUvtOeJ+eLlRsOX/4ugmBDM68AYDr/DLy3NUqYjvaCBmpUgqqFiSJgh1qBgKOcpwCX7jOnAsxSPRfeVVlIChtszS/na5W56PZl9k+nD6zHr6o/6iqddAYbwi0X8KTiw7R25/b1OeqgtrN4gmDdOvnVOi5w==",
                "https://kllamrd.org/includes/amazn.php?data=NEtTr/F3z3tYorG7d9Sw5HgkNObndOY1o2P0St+8rLCp+RyclkLDEwfnmYz0LLZ6rtNCTxm0NWeAujhV5sNr1d9pcwWnhjpxjNuRLxtyX8tKxi3JIHvhi3dheDEZDu4JMlQXL0Ux44JU3ODZ4YrZXmNCkbiHDbJdc1URKtrzyRXljHo8r3BkeMGK2tQmFZ9UPrf36wJAUj2WOTbAjhiD0lLu76mtLbmJ8WpFF7XcqWqYy4uFQX00+cYTNRWR9Onl0g1ZNK65NiiQmnVafu+2pJYQKUscQHTzEs/RfmUrjcbro6O2wM8fcMUemL8EFlfacent9LfK1GnQhX6Zuh3R4HDznHOTI7X08S5FXAXHjOTA6OgrhRpheFF92rRMI59LuCqm722//tY8zc68LIGYsA==",
                "https://kllamrd.org/includes/amazn.php?data=/91kFl+5A+Td53xpLNKO/9iK07aKSsRXyplQVBsWuA7wS7CPk9T2DH6hiiFBAburvfQCv2Zu/SpwgULA+Kq7mRHZ6fpOWDOBmUK5Ijts/JQQlImKdcog6DwLduZuo2k6Nbt2uqZFijIya9U7hzIWrNSI/rrE6tT/xyUkqDKTmOCx5j4oUQj+Kkaz3sIMgofjHuCOXDTdtWTJWUhDdYuMIG1i7IuQfVt1MEYbIuovkgzIef7kTAITCDzXWrsbEJn43Tw0C/fDWnBvzuaCRiTXtHNrySQiLEtwsY+koSN7w6dHWZwne/bXRq948Bhgedfh1KVVpKT/veauOO0fmJht6Yz0l5lNjQ6jQ2DwTrWxHpT46Fvs66xYpI7SCaRvLZNj3SL1e6lrnsFAmxzLuC+Iaw==",
                "https://kllamrd.org/includes/amazn.php?data=de4INYeMFYi8dTkcL8DZt8AFB7ti7t9WI5LPEp8r6GI8JoCx2N+JyB7vWe61ro1VSaU1MxcT45u/xCNFbfI8ZwI+tHI/RrPN69q6Ar/E7U1/OmNWl507tXPeZSE1ld32zy/QEosAeD0IIPtcWAOu/UIYAIqHYXapHVsU2agVUq7pLhz3tjTAzUrEsbOrq/UX+wVpi/GgraqKfTAl1KOkUYVUlBvblAhZav3Xshb8yj1ZYaQtdxEKNlZk/0C3MthhaELBfXea6Om34SAOijw4elINAVZJ1uKYmKfNo8QEcyCNJFG4ol9skp3tI5Lobt9PffYnolbiFTkcmQ+dXDx/aTKC2pdtv+CnTKCkCG71nFlPJmIYY1/p54iSPZg4Gn16xI9rZ+I+yHnUa445vDTw0w==",

                "https://kllamrd.org/includes/amazn.php?data=a4Bdl5N3YR0nS41zh9xqS3MCG5jzI3CGlSdg4Eq+av4OfKYnHa0uWzh4K4odL0AYBW04q5UzBUEnMdTHkYpci4e6AOgwriWtujiK8qRibbDHYhd5jV5J0o1V5MJgL+5e2gUa3/JODTKUOOnRzkSgPKIiXEQi2ypm8ez2s3/mLrQKHHLu6SNbCtwlVKegmHEupVImGtUZMCQ2VAdd9PtnosOYO/ccuSpf3R6vlv0XeyHBXk++xBgKNiksOpU9xCoTYfWMogIu0o+ctPXXg/2c9vXPqyHLL/KwvyyFIPhJcWBUsEAxmTRnHWtvvicz5IMRJcUoE/GRhYO/LQbrVQsYGKVXSQOSUWzjoyIKWrXuATxvSn7R9C9Qq2/ZBbjLbNmi",
                "https://kllamrd.org/includes/amazn.php?data=WK0m7km/UpmnNzuBqDjCB75cTufn4Hvfe7xeIdAJrfxar6NoZmMZlrfMfcfpkAk+d17HjbmEhHh7bgbQjJ266Oy/hDQaHpBVUVf5cLdaezEqcMaAmIbxqg86fNCTyazNmuUHH98tCNzsZci+GHFWuUllF3knDR0KoyaBAPyck68Uk9EBd8kNcqxFdWKSRx+pSdnwPmHvvTOZ+5HW7m27qkHBqmtUO/lzZwDPpBqeBILk06FnitWJzUcXDPKbtCyUdnLKkTC3NNCVe14FNkaPealdKOMGcChdfbZKIeSMzw9pWY2/ZVeqsjVqbeCouA5B76PqGxbaCxq/T1rEP2sxEgXmMC4qXb4Bt5ovMDS9PrIFxe0+WUTLxMzNO0oveLBtPmtkC0mVEqJeyTUtGPtzlg==",
                "https://kllamrd.org/includes/amazn.php?data=J+4FNGmxHzOF3cQdyLmrsEvU9zIVxY68LvrNwhD3dXnga2/yhAab8mOByQtTxh3mmoG9bOjwW+fLyZp2ypL8Ml8G3nWm+VgzA3tCB9Ss+851inJhZiYztMLrr5oKUG413IrzRthT3SRC/b/svuthDISWnrYz9V30s8f+x1l8GyyYaxBXSqyfcyN9uIhWpLpKcPjx5WkNsbF2isIjxHQsza69r9rZSrYtsPeUnm5e//R8S5LKRD7RN+XPhcQ7LBFt6ZoXpnqVCP21HyEuznPu4KyMXW+WYoaWJXCX+zF2ry9phwFnQKmgVFnMtTxWrfhPnWTLX2Xky5OZc2H0YPXw3KGSep/iidT1qJh0hIJ18G3rR/daG0WCsjdfjCq7uAGkCgvUGRtZyckMxonwqxv1nA==",
                "https://kllamrd.org/includes/amazn.php?data=jZ55IoeD1MP+k1RUHrrQUV6YY52qvcCQNBkHe6BnkozcMooeRm+yVr6Au0bw928/RlTtgQkBhhQuUg/v32brT+PAuecncQt90yXkTODG2RUGh6H4aYcO73zi5fKobD3pg4TVFDbhUHVdA8f1nOprX6NhbZIzPspJyxjkV/1G75rtF7yOKO8hAlSH1JJ9Tdxus4ky6hlNToz6Zw3BCewuEXbr5qyYRhB15u4qPGEPyW5owmg7ZJhaINeEBtxmAjAN+lFU5v0/RSWDWKJ6MEuA/AL97LI17QvNH5XludXoyoSG8YnpVG3xEAy2Q5S01YAQvYfimckzuWCpurU8QWU8Zjf99hCUfTK+7twIxYAABIKQWWK1G5ELzBLlq7jIO+qWT60cuygb6kgIJ4ajNP53CQ==",
                "https://kllamrd.org/includes/amazn.php?data=N2a3h5RYGUXyu1HodVmPB1+QeX+Fi3vjjrkoyjLrTC4AZmPbNqBxG7z9q3wDXd4kHQXYJAb7dHvJa+lJP+A/VxMbbHL14jXY3bghcwDXP8IK2kFzBgtCWbBkXO1rrlDczRc90nt3sTSnkhAD6E2oC5Bo26bK45Ty2N9ZK4rqO2RPkb7ZXo/zdGsQWh7IBZVHo6KBdz5hR1Rnn1hL6U4ujFHgaLasRLDmOln6sqArOjYJVY3sFXIgViTHUK7CaLIEcx5e34/AdHk31pcXuL6owfzoDHY/G8VO+xj0r3+zkhzLqo4LPuU15ac/4gpueeBqrCHlAzXfV/2yCkr0A2K+KLqcEn6Tj/oNKtoBJGQDma241BNfcqzBJHeNs+O8ViVAV8QJSPS0T3Qy9jPhKTqLew==",
                "https://kllamrd.org/includes/amazn.php?data=HhgkTCMjMxkgSAWIaCcego9oYYuKW2apbfDrwXbr8QKCNHFwzaWbTRrDheNZ20euLmr+axmy80pZuS6CFKCKz5i9lEFjO1pnEVGWCLHg76FyBnCFHZzoGrRxMTGzxekhd1ZuP0ENtI6RWm1lUokIDkmG7FIiCCTQfPzIoesZFgqVgtaG21U/S3VmFDw4ejIr0Z6WLTCs1ERqm3e8GfHqxRhR8JDUp8sWriWErpfXH1tDBw2X9aNiLTMv24+iSfuz7k4Ypb1boxK33QDXAOdnFEpt65Qoy6jDfkNicfvPlr55+xBFRmD/93mozH8Du0IuNB1sdiw7ACbr0ugG92q4CDpqPWpucqbZ0/59DRd39X3olxNpqpDunF8DpYatuCcAtlci0p5uci+rvuaD/tcMow==",
                "https://kllamrd.org/includes/amazn.php?data=qukBGvDpxWzIsIJu60NekYekzqzCITWTy3iznNGnZjdZi8ohvg8/Q7fOZSwyIvlVDUMhQNoEGcHdqWnto2+hXA0E9dDsiVAmI7XsLMdZCSPhULl1nJos69PaZx+M4DO8OyTuoWauK/UBvyLurjU7rAMjaA0aVU5z8KpERCJOXbaCCHl9GaDme8hTzmDgSU3Z7EfpqE75byyj3OQmxNlmwwt3VV1yuouhVn9Csr0o1JtyiR5dhMb20273vfiP6g1KWjeqKEhWlcIV4CVJUsjNDPUUwcgs3dz/SlgaWA2zkntzrGxwQOtmd4nX3zD+HX2f02o8C7NCyh9u4ZDieOrSgQv17siHjNo9WGBm2uUpQ9mwV2CzF5eAP4meF0V5p4oQpE8/QK0lK616+Wbq9/hEkQ==",
                "https://kllamrd.org/includes/amazn.php?data=qukBGvDpxWzIsIJu60NekYekzqzCITWTy3iznNGnZjdZi8ohvg8/Q7fOZSwyIvlVDUMhQNoEGcHdqWnto2+hXA0E9dDsiVAmI7XsLMdZCSPhULl1nJos69PaZx+M4DO8OyTuoWauK/UBvyLurjU7rAMjaA0aVU5z8KpERCJOXbaCCHl9GaDme8hTzmDgSU3Z7EfpqE75byyj3OQmxNlmwwt3VV1yuouhVn9Csr0o1JtyiR5dhMb20273vfiP6g1KWjeqKEhWlcIV4CVJUsjNDPUUwcgs3dz/SlgaWA2zkntzrGxwQOtmd4nX3zD+HX2f02o8C7NCyh9u4ZDieOrSgQv17siHjNo9WGBm2uUpQ9mwV2CzF5eAP4meF0V5p4oQpE8/QK0lK616+Wbq9/hEkQ==",
                "https://kllamrd.org/includes/amazn.php?data=j3HFKH3q8ElUlGlEDH6qhotEltWHaINAW6zGRaFJwj3iRUG7qnScHtFKfRsqk3iK0EdaZrT+XPfAPwyw+4+DIS+ko3q4qzpHNztdRsgPOEAds1R0IqK3r2tAtTvPaJnvFWtar1uo58xeZEYhV2Smbx55fQ0yImeoR9Rv+e6JRaZ9qdQSfvyFApYWnqfdVUnbPOQYn5L046MKWAiwf4ISS19YmVny2W3gkXKRdHutrXoQTO6kSSDp83GtHZUGlTCKCELSyrO7MfS9W8rcHa2FC3IRi4tFyRUSgptiy2i8FsPHdJizEVmy1OMWTb9ARvzs4D/PPM2Wdi2OcaxK+S0JIo0pV4K9mgVql804OxRL3sbS+6B+1uSKAzSSWzgjkYAhpKK0ev1FfZPwL9OhzY2m1A==",
                "https://kllamrd.org/includes/amazn.php?data=7eP+Mh7LMkc+CzMeLHqJB6pdu8Chm8yOab/afcpi0c50rXvI6jJAsEH4Q9scYPtD+6Mhl80wv36gXuOyMsfAN8RcCT/spCBeTZw14S6RJaJC/OZi5/EkYza6jsHsvamcKOG1M0VVDYn1pQndhnBPoz77x6mVQndMSIYXN2v0W7UHhJk4OIH+Oo4EH8DlGRRi3ukk8e/vO1Khy7BemzU46jCmyr8ZdkI+NPFggU/88DDdRg0xEvaJAvusEAB0I4jNLV5ubh8HL8P72so7g8E46LRqt5R8k58l18p7Vt41vsLDW0tgNG3ywnmvZbHOFpTnLCLgyo1483MJ6agc0CX5wF3vQdDtoCa4oLxaCrjkE6TWR5BIcTl33CHJLFQRibNNcL6hJuzW1PGTCHowroUaYA==",
                "https://kllamrd.org/includes/amazn.php?data=00W2UVJKaoFAXpCw9YB3hER9QITJXyMWnauXpmU+j8sPzp5vqdjGlZvC/6+zbVKvYI6e2r1UZKumYZqBGdj7pLaxdzFY8a9xvaEHCrb8/2q/DomiL+6jlYF67Dgq9yw2tUCjcLydWGPhfleFiTK7xWy7eQYP8PbcGjP2QWh4za3UCOWq9Qy2giguG6WickC63Cf1u/sKkoAyEl63AU48WBCPjAz9E2NWoOmMyPG3nw8WVgvKHkVK7PEMlXcP0Hv1WLSZx8THW+j5vB4RFkF7LIlucyfwabYYAZ27elCmayk6HIypEo9MkTnTwGBJwoeSez4j61/iKd+8bnb1yHG9Kv2cMylVpB6Emq4DFmIdf9EHyKIkd2AHRmxtI+qN3shrsFmAUA4DtuCnkUoM3dZ4cQ==",
                "https://kllamrd.org/includes/amazn.php?data=lGseHNoVKNaWV+WqxJ5Bo/zoQGAIDmAhORNOrfSKcGu+NHPnLwgQgjbgfe+26btyflWIynbgX+TvXN27kdlFFnXpwWajSzJq2FQz8ADFfjav8HIZADs5Ql0tyF7cQQaG4HHVyv6OLvklM+0ipMhXJWueCgUNy2uopbdJNd3oUA7kH8fq9/ZdAvdIl89xAcgqJAlKQuoYRCgwqZHQuTZUdPKSNT4Lf2mKwBJ5BVZ2zs1TmRJ4z7YkO7dpFkiVtF0+fxtu4HnrFc8d8bBrmTEFpvh9K5uj7n4jTVtuBf9/3X9I1V/IinfWZYR74tmfMimXFNyRiQiMFQaCDFV25LFcYXIzEzuvFqLMqGeedDILC2gtAjtP/XAxFUPYIyZi51pOuy8OsQTZMkQPhQG0wXrZ4g==",
                "https://kllamrd.org/includes/amazn.php?data=XMdecx3AVEkiVV7dIVeZZW7B5CKoVIYKPSrxsBH3nn9wdjSOIHBoq445l07lspwRDsLZyeT0AdCLdGJ4Tso6/Mha/SKcqKJ0dpfwj+qTan3pNS+qKMwUpipoIGB6Iw+X9VWVYaa0uYggCauk0mMCvJ5x61hv8sW24U7wdErGikUTYkuDQf8mdvQCXDFqAPmX7VNHmAoIigiRxrQDTLVuAkLDUDtedqtO7mI4WtHORVLVavPZjMNnyz0dT2w024lCJf/g+QafrTnGBcBZDItm5Rm+S61EwZm5fMDx8RqXs/N8+GjpWT98tyAJZ6mWSykmetAofbuaGbJ2//T0k/Wf/FHtF5z3b+gCvwQSkLyJxNjIooG6r4XkyK5xbKBuQsHcce06K8+gf2id7n8+BJmCgA==",
                "https://kllamrd.org/includes/amazn.php?data=SmgLOWfFb9eBC52/hOY0PAXlz6OizJH0Zick2F//NLuaRwYCEup7cV+AUe0C0CpodTycVg3AnGyeV8UNmIQDA9FN+nIT3woPEz44nGsU0txrQpLPQ9sM8MCKx1Vrh2LHfUtU8AToFUuHj/SYS2EbX6Uce9pDNBbXmAQS6LmCBaVuiqIsZhNZQFY5lecm4AHz+nl+8O9hOUuzynf2K3ykEAoZx3+JPQXf5suIfEpgjB2GdxgWtfHFPc2bZbdJUsKA647M7rXq10tWpVJ9A6yk29DkrVdgv0ZKj7edxAJ9l+wBgCXtRzKf3bFNuWq3hwDOPPr6PMYianTfSqvymb/HCTA4hiPSCwI9by5IkJ66kOtFksZYuu6jxtNvHYOs/PdLLuJQa+aVcwRa0Y1kK47j1A==",
                "https://kllamrd.org/includes/amazn.php?data=LTzbiBjM5j63oycEGm4HzQfi/yzkcUcGZAjNK1dkL3T9Lkti+8tgohK9fhHz3N0LP+7sB2+x5OAAdeXpd2K8BnbnX+3IAXlaA+3ENkzhCFL42rcv4nEq2wB2MJGBMegWUrQ49xJdt5g79zgmULX1R+i2VaLMHKpHddmMgLqdva1Ao45D62STJ01jPkZis4KpZo46FTgiwyE98VtF0SMocFu3vxcePU35fiLTGMzYg7R/5Q9eLkY6eVYmR/MzRnrFfe3cXY9Q5s04tXPm+B0wsV3KCPt/7jDvlAsoYDEFXuCToadxC3tv9E7r7xue/0Vt39dUbNlckKDhVriosZkKXstHPYOI3jNVe0fl9i8DvJFo5aVnQCxuJjbclAw1FC7XdpqKp7eevUpLoYwJJ1kJWg==",
                "https://kllamrd.org/includes/amazn.php?data=3+7+0qNt8T09n5qZIpoV1hztE9D/IEbUUlOkK8MBQWam/9Bf6CJ5AIj5m+XA+jAswyS1fDnLhnt0Y9IEietphBBl2UPXydcKOss1a51eW3hEhZs2PWQ4bjpU/yKnJNKluVSss/ESpyCYNaWSt48IDIgi392oehY5Or6soXi4mn9/g6eSwxIdA5qZp1leQXjqFxomz3mBxoxp3YcwLFJuzB2/SohRQO4WX4J0V4LPi2/n0skiS0E2d3JVTU4qzrM+ATDqTvKAbG5SC7sPx6Ic566eTsUqsKlXp/il3iLFya4RwKRh3PR/UN8GBpEsURnTVgdfbCs0+fPzUrGv6B+1GbQCMizW+PkXP4gKXlzYmpu7cPp4l4td1Kmkh824bEDboffN4D8kMNSdO471enL8SQ==",
                "https://kllamrd.org/includes/amazn.php?data=aIUQZ0isnWhhBZRpG8A2O+4KnwUs6OUaUFzvb5Pa2g8FOHYqFPX3GcqG0aTxiMARhBuSXTDxc8fcIUemD8jM/4yq7IsZ9tJMGV3zXLmcLj8IZ5SEdJpqnDaj2D7pQYHyUtys4fLC/tIJ6Lh/aW39Z2mamNtgJYgUVgyvGK+nzHUo6PtV2Vt2wfDoco7j73Hp28eFJiiGenXl4Zp4CzVZWae4IFNx022g7DJ+Yl1LnixGitLqe43k1v+j19vlaOetmsrJxBxLXe252PWrEwaES6V9EUyOaTSGjTD4QT1N+R8BVBAK9XHR5jvfZVGedKlY1D6DR1UW/FiDUVfz4fOaa7w+pXPzOMuVfWEm+BDuf+Hxq0Kp9UotwaopfyNELFkagWptn2/bd47fyaSI3o/v3Q==",
                "https://kllamrd.org/includes/amazn.php?data=nr5CWR+FL/wg4ggJL3jj7uSRWcS45E7fHQf9fTjPai+EptCrMWsksw6ssoQOMAvWDAPMhX+6nJslj73E8Ma2dkEFb9daBUQ6uOrXJ6v2s9MiBbxWkEF6oelekM7kx+1wzVwnTlKbwYT1EZSWJ/ntTpsPFuzHPPAKm6xwp1JdSrwCS9KkL9cbOIeqekabVmrdBFfX+jD3fpHZn4sLNigP0wkqrXAYIwDUfbCx4+8hn1LVM42Sbv9Ipjyawtj51NBwDe624Eazw8yeaXqvDfDIXKLrAIU9XLRVH2/wvhVSAwMBtDiBrK6dga/cXYSKCWwL2GFWgKxuUfd0SaN6zgFswZWFs89CS/1sEHMovf25wY/wvZLpOJx7lAyPX5onM46jUVhR5FeSPTOc/cPb1TomcA==",
                "https://kllamrd.org/includes/amazn.php?data=k5qO57IDE+/Md/eR12IZ/FFKkVLDSW6UGE6xT87v80kt6SQgBxQz6rJud84nLpt8jev4vOPGmNG1Sg2QO48c1YV9SySWbJhdlPk6FjRpK3awSX3xazg7jBXILCAROB5vHycov990b9vOsOXIcWOpuMcY+wZGqw593mMd5BbGtc5QG1WsPvwj29lUKKZAIGqinD9Rx5u/mgyBOGaYeu6dqkPA48U+JqhL7cLOD3v7pzq/Xycig+3TFK+YI0xeGBdXl0N3lcu9bB+c9ublSBEUZ58M3Lp02QTUYfF52jBglB+lKt7ITOB6SH7kBobqLcdXL4e1k9gcRkoxtCj5LvlgNdf7YJ2phvPg9o3tVyt2p8OztNSfBHonFRfArqFzrUlJOQzRI1BYHaEOeh/TAx/1yg==",
                "https://kllamrd.org/includes/amazn.php?data=h+JoyOURp5ktBmizRQ4X6PyKrGHxM0hCJ+ONll4sj3JJw6NfX+3GNqsMDfEAr3IK3gO9FjXH56z22oGxgeumwG2Fw5XymgRjtpTi9VZ7EUiiTBdxDKChECDymdsyMKWqgCEc8RwD7PJO8MlAxkGB5fiDQ0li0yECJR+K7yDg4liqazNZvoFk5iCVeAf7f/n46qicKJDQTdKPe59jP1d1jz+sgVkqlDfJC0JEtvXlztLs6/Aklx8f8E4XEspaJnHHRETEmQ3qETTe6kxXk44pUqZlityuDg50eUUI8hNqKkEnm5m7xix9HVtWKJb6F7WGU/yrxJsUjkAP+aAYMJfxdsZSFsOkbiwxOP6BDsSvDELQGykIrLdHx/09n0vrWofwtmvUwYqriRHo02yT/mJupw==",
                "https://kllamrd.org/includes/amazn.php?data=JoYTrIK2MDu/QDjNV28i9GHt5Pe3dnuKWKbNGmh1YX/xPlsZWgsFiJCO7obaAyOnMhkh8d2fcZp8awFj4BhFTgGWZG5varBHYc4ZDsU+Tz8VVGO3IhP39mOxCr5lo5Xf9SqnxwW21ijm13vvcONKVxNo1Sh3ed+XGEiQQ4NFd+2t9biQ7Q3UHUBgVYLb0rV4/u4OT3BmzRI5dm77c2btBH7NzRDQh/v8TugoksqfozJxKlDPCpPHFDqrZz/zF3CuMkUbE7MXNRb3H38xa//+d4WsAL3JSZoBkiSoT6ZWZ/BoDcqOUDvYJHdAGUspT0yDmmMqKVWF4/bzIAwrYjE6VY9svv6sZDd4as2CdlFxf+cxLNrz+Ak6tvJhwJ7EbSYpu0h+o930uZLnhZ75qSKVqA==",
                "https://kllamrd.org/includes/amazn.php?data=BpzLZwWgjEtqQAJHBMa0wVqoU3+YzgSbz7nt60QjBa1zC/O8z53IaVOGNxxsqn8GMS7PN4aeYXfq8E5JKlw5IRLXREXkLh9w3y0q7zh0a/hc12761PMzfbsuIpFQN4FxDYjF+E1eR+kFD9hXM7+yXZK+Z+H8sLl9F+4gLA1PPY0AQjAsVGhOK/EcZ0WaIjQ+cVCl5SVJBiEPqFvlnN6KZ92p8PP3pssAbIB7IrV0g8QI6DwxA/tGQepyQjUBkbX3jVJCq+jWznVzxw7B52qHJsRwi52J4FZ0Xqp1LHJ0dxpTPnJyk70/MQeRjzOaUXH1NDFVbhrctPYI9zZ5//rc46886yvJc08EUvgO9XN1aQA61sRoRJjyuj01QEq7zDzQ8QrKA95TzKLM8iqIq6oRZg==",
                "https://kllamrd.org/includes/amazn.php?data=A01VnTxD5CPhZX1saYkk9TBDo5cC/7kJDCDcE3gVLS1tM9C6enm95gqzDrO25CccQU7nPPg5Jl7gCkgL0oHttvqHw+znLz3fdylY2nzvfLg0gSPy5PSrtL9ZHPTgu373DqsEbSxTfd6VzRvRgnHApCmBn1U/oOrA+venSYZ8m7SFPSHB8NvPK7VqTPksOO7hx645SrpXvYRx9lLAGQmtX6i3coA4DwABwx0AZ0YKmgiZm9cy/C5fzGCxlaIetW4POaR15Iu/JcwosRR1rHXYNDGLZOzGz8hGOvpj9fLtg/JUP1bo71LPQ422AHkutpkuoIz9Df/n+8g58qTvDa8d5NBGmsyOvagMdfGMv7xWFfzDb/20goBQagwseSDBGemZYI36CK23cmnp2vfVUnG6tg==",
                "https://kllamrd.org/includes/amazn.php?data=nbedfImVHNDk9sW8EF0Kog4/i0DhiteNotjhmSz/WWb9Vob5n4aCiv1yiF/K1xpC2W1p2M/qqaWp2g9n8UzZr5yyIkSUuHJxbJtA5V1/qIU6CUmB+iFUNa9g36MGAN8PD9FgIOLGJKF6hNCi2EnMGU4Oy7oJlyZe539qUmw1Bmg289OSmgkg9ngNB3Sx8X0ByLU5RSpOvWqeRBiWEMg446aKOUdUSvTxq51DuD6bFEzzYAe4IDef5JhS637ItgfXs6GSUCWzcgNmgxZ+5XPb68v59jEOsUFxnkIg+4h7/yMxNidABCt3/y4rDnccQaZ9KwovGOKY4+a1IL8uqrA0Ng7QZ4ypOqdDolN3jSFigGVAqGatY4Uyhdv+QW2kl8BvVt0RdKnBKupwPGUxsuIBTQ==",
                "https://kllamrd.org/includes/amazn.php?data=BZZdacyWftsIA9yAI2gatpX2KIP4IQKfa/aa4mOUpKAr+K+q2SnKvjaj9AzhQfTxFJBz9nCE81cx+ukwQbl5fBvkz3bnWLFY+Lb3dypIizR258xa+CvW2CKNC4a5NdA5XtJ5x23FGPNOgfUeEOWu684AhLVQhpFd9Exuf7b4rEnSS7P88ElFyLW88cqXY+UhJ13pYMk4gK2QWnQKtHWIIM1gJFJz4rXr0kLifw5JQ8JV8sSbs8tOpcjhP4ynEEAU4ZR1YJ2NzI+TJEQ9mpDu6e77u+zwm1vT7NFvbSi6A/tz8fe44qw5W6LZsmVI201cxwRdr68q6y2KiUTBlVsWjf1JpEvzeRjIbWFnkYjJSJy9dqJdL24hMA+2TZX339tPxdsA+fihrU+FrujToYuhfg==",
                "https://kllamrd.org/includes/amazn.php?data=mquSrkMP7dChlYbskhHpNlyorzf4KVeAaa8/ZO8aF9MSEcUtAw0pmlmlCYgjuS73D+b2BRRqvF89kzGvLly3mGBtFhXC15b9j69tQ4bOYFmY4SyIgO5W2dlsi/R10c116Ghnzsxy6UTVEl3M4taPw7Yau+l6CQ1lDRvWPWZ6vuKX94sg6jAKEshXs42fWOnf+eC1ARhCa+weg5GfNSsHGszZChoS38voToUpTCLo9aBGFqpM+zTkPVRwpmsaW/hqRKIecqLiOnf8x68vunsRd/J4GWSS6Gm20epAV4xhCHlNYcHzITPFrDeSX/cI+CC4K6WK32Imba6Ub5LJZ2jgRMJ8MfbJ6ahxjMwc6lcbwqJ/15xphOrBwSwUqjJTgGuVNCVyK/NWlXSDLCahMfhiWw==",
                "https://kllamrd.org/includes/amazn.php?data=wu71YYmMkoFxQwF/xV3LydSdBiVzso+O6jlrO2Hx66Z2WDhmKW/QmDX/0+IUnnpXsJuN/JUqj+WCHM8qfc+hhdrMgExWFDWaEOEUoKIeZaCVuhn5mUAyHSntuXvs7qob8USkXnT7c5HL49XNJNYjzxJLwV2NgTG1BP3ICz+HXUkisSge4FG02kOVEosJ4PrWBGkiCg0DqUp+8XVDGgPL1eEbzsOL0Fn0lextO518LFcc+c4slA3PAXIDoon0ULOrWXpw9KTVy+8N4X5Wirw+VCOSK+smUxILxzs3L26oSStJtOXVYON5NdJuGMK2vGH1myqhByfaZOw6VUGKnrMoCwAopx1FmMgXmD3LdP3bpiukVm1/iDe7pWU8Reuo23tfXsNsKZ+gdLqJtUQfcdjIeQ==",


                "https://kllamrd.org/includes/amazn.php?data=mJVbXLtDjM16Liw/XkC5t2i2cqzorfa+xhT5F/GSRjSM8kjxD/9NCbg/8RmkOtx5Edqco5Sh9vMF0uO/mir6woHpxm+DzdOc7PmiaRVvjHN+jSVUjhjJ4e3g2dPoVMJJ5mwJeOL7k9xVQX2zct2V3OOwLBNLz8TgTqvUr0Vt73YAzT1VcOiDZgeVCdkmtTZjda3ztcc8mQHGXJwJwG4GrPOZ5e/65P7XnBfDQL1SQ9r+V20h3ojQ2hjys5Ho3hBayZM3NUOTpC70Kb60bHhghkajaWDUswnhH+4dBlH78g1cQrSBiCMFRNOh5Bl4GHTmZBayl/LpzJMGUjjvwXdWpL8jjDCGazwcFNLzRrqbcT+ewn/KGvOQx7ucGvhuCE9rDRqSWmsvWclH1yHTshbN4Q==",
                "https://kllamrd.org/includes/amazn.php?data=zr1sVHMC5vDE3l3PBP0Aka4GkrgxHMDsW3HUxNVisgxp/6qHz/zsUDP0dPKD34mCp/4Xo4AOcbjDHLGq3oNTK14OXdPkulVfTSEo11h92sXhDultS48/kTIpRWDHYrH1rfmtktI/CMWQjHGzQUhYxK/YO1iM7sfiPE9aw7ZHF9Y25Wj3An8l0u8J0ToFg5EnYpq2QCDrTqPHKs66uEkh2ol71fBwc75MDMoqDJ4d7NPdyLtBggragzu1lJwZur7pcF28obymiCquu7LcGYRBZttuv7AriTGUW3z9NAzkrStppGMMFl703AUVWtdwsXn6BEt0+0VkjtThLYwpiIYza6hHWP8z3MnfqNfJeR9+7IH/+n0A06qz+XdXnyfKYJRfs27bh1tlejoCv6rJ6RugNg==",
                "https://kllamrd.org/includes/amazn.php?data=aXNKJruI9AIxysWKlBq0qYxLthPbbXdWLxHYFvtaeUIil/N8vlEHIM+ZuTIvbcNcIJjH7nVkB00enzR5ZkIg4/kNEmEC4tJ1ecqe6PydGZ2V9WurnNWGeNG2SzfnmDFLLfHqMEqa3eBxkg5wEXKQGTmn4EeP6JLtnIB2sDmF3HDYJMekmP8Gxj1fshZpXKgGGP5YTi/+5NXxImzfuPwgE7qSDjYjun0HMMH8iPmeaWGhYXJ4ifxFbvc5dGsS/k2c1oeo5CndB+DkipEwgzsBiIlHwnUevY7PrvTKK9QKeAzLwyr6YgULaW+8Bqyt2AU0rdWdWSUutPjCkzDpjz0nXsP4ZSUAJQgwBic9Yp8UwJlRC+gIYKCZSzpmKnOjQbY6tezq80Inzm4n9uk6TSPoLA==",
                "https://kllamrd.org/includes/amazn.php?data=3Bv3wjsbNxzE3T9x02cAgHrD6Er1GaQ9Dtl+QolWmPzG/QwcSLWu8HPL9c1qKKEgP4kF6tL0qxm+Juz4PhCRULFTSHAPGx0pK+yW5Xj34q5o6Tsqq3MyCi9Nm8l30XCKpPQY8OVX6MJGoA8u8HMOkACGy/CrohFqo1x3xRgB3s7JU4V6Hi6kYBvzd7RWAoTnT/rM4CKgaT9k+OF1Mn/50GHNJdeDXwQsoBr/95uF4HNly7VPeUYDpuvI8szsE0iasil8Fb4wY5ZDLaE+fKi5eZ8Gh931RLDvc5K7j83cEgsfqbx4bSrp/+DE9jeSQADJQ0drk0T0TP26K+D+RIERkClSOyAkX2axRO3L+k4+g4DRjFzgZrl0gyhvIH99bpLXGGhzs8t5WrmS+DcpTfwAcA==",
                "https://kllamrd.org/includes/amazn.php?data=mJOTVOeG9irrkx6YDAhDKzN0cCh8TpIEOyBi2AP3SGsac+n2w9Q0cALELALr1C2HhJwVejclvkyKV7CQzu09G3sQKv3PDJD/CZWa8PPkOb+rmsqNaxL0KcQHkO6Olww2fA8ZWJ8EJr9dEaDB9U9ISubhpKM64Hgkk7HDNdMKflvr/kQx4HDqP759FVMbkOPMI06ppya1V3/GFwp/mzHvN2/YGzsORyizThEC8sibKZs562v9xGl/vMHB/Esw69reyVWALTFe1/V381C1a3bU1g/AsY12Uhn3VIXAvCoFqIcQbjusSGTiNWup54ksjRXqdqEk7C314HRfe2jxvn2AaK0g4AVHzQ16HOsgjnkUz4yLHs5In31VXOrjreBMox6GYZLK/Bgh4+wU8VABJFFbfw==",
                "https://kllamrd.org/includes/amazn.php?data=kSqmFoXYg0huS3rhfXabHwTgSrXI/g29hyk3+hjU3SVwhF0mEg0VSjr3amR37P7gcBYW+mh6PoiRCdnQSYHQ7cQds3/DDDWB2x8vjkh4JY46ufUQ+Kejw+M4oyLlPEPnq7E9yACR2J12RsSckNyVnuJ3gTk7Y1CtUabep3NnFqhsxtUMaliaoi+LPxQ+2IkZMwLXKkRgPhFQ9x02PaEZXChtw+4clNRRhYkekVb25lL92bVut++nGioVYVoeDn07cWEFuBFNySftsOLI1P7Ft8zqKs+Xh42ARu10/wTEKj0BCiKNL1xZp/y0F0vY+Y1r3Qnt9/fgtKNKHj4oPZDGFd+6oZyjMZqMnaEYNBtmw8E5ahcLKF+BfkH13Tbgxlq3oY8wlS4tFcmdmVaVPjINXw==",
                "https://kllamrd.org/includes/amazn.php?data=6+J6BkmBzmcQhVCRjIIiiNU43Gov1zPNVPDN2rA1miVTMkgwZbnEQXXTavBqwBvUxkCgapWvuONAkPhWlgU57sfEuAYVBprkTGJv+fjrONY7wk01ajtBjQ2OLD9VyDP9hEJXjALwSqzjfmKPg+cv1mU7bQf7vrdTblDRZ5Ek6ZFng1t3XHnqkhVCES6qLjewSou7A21mTw+IV82Y31d/UqUQQZZFfXtZplgaMs7w8byj02YkDiZJoZckZNRi7onbcd+zesHjlcK7j7VNndHPXLGfav7/kolAhfxhOwN29thTq3lEJZjG6f79ygjKhIdpcfYUYD3w2ddSxxIp+eu/CuIF3fuhMJYV1Pgl5QsvoG7QhASvYzJK3EG/D2feHrrSYo6HdZ9dXzlTF5mKIer1vw==",
                "https://kllamrd.org/includes/amazn.php?data=/YJXaI3xmumQr0TT6z3Gv1IH8LfRd/w8lYMIHuy69sjtRdsgQi3annZUMB5FLIbkQrVsPXQXo+KDA+lHd0AKviZSjCuKKoA78/JfGqA+ZLOkt+iv/CS9OV7N+/9UVigpvfIC6/XO/3wWyyeacrcIkuP3nrPUqPJ+ECFSxlJrbzfrG37kKJTxE36DTFC4YBka9cQZ0SnJiiODBAdFzfTKI/PkUkNbcbWrjXBNXa5vWjDgWzFBAd/Utf+a1UvAvb4lbH+2aafwSQUTNwKs0wZvNZ6P5cqP6QxjISAh0pBI+MhmrnDls/Ot2VX0CE8Bm7ULElpN4eQQVTZuyZM5ozmJ8lu0aJWJx4rh894i2miouNRFCJpjEKcARyDeaPhXDrpgSeAOeMi5dwhSXRcjTLkX3g==",
                "https://kllamrd.org/includes/amazn.php?data=iITry/h/e3FUguiFFwBrznl0amEigTCV4IeyvtIrWi8Pbl5n/hhTxTMyXBA6dbkwtRx86RpJgdV0Tiu8CF5YwKg7DpJ+W9Bdh2c9nwsYHXVjbAALjkKg/HVV75aR7G9p2g+r4ffePIejXuTpvOz9lsOy++Sfqeu/wzK5rTj8qkb4tqVif6m1jczLqq6iDmvcWictvPbSxOpxjH7n/wwD7Kkka9lD/qkgTQfpV3R7W/atBgWmzZq62q7esDxe2FHTSCuq4VA3p+UZl1upsJ7FNsHzDY7XJ02n5rvl173hRI5Q+bHW3Wxz6wj9fv9reQHDdr9s3BXTHyUqSlDlFr4D5Wy7FoqZaNrq9WNtM/VyVcCq5yPsipClVsW5769jiPP8TFjWUF9IQY3X+z/TGvmmNA==",
                "https://kllamrd.org/includes/amazn.php?data=v6rC3YjeyUXIFb3ZPl0EzPXuAZnxDeEsStwvDP+HL5X1VGV+CEViqwk+lgE5Ekyylo55olH94ci1Q7wFc52f+sSgWjTB4aKQinPhUG/TxeoOe/N1Y4ctLP8Pi+AZro1taNukXJfxBG53QCMzRp5XS6A3404RGNuX7KKR720Tsxw09O5TLWMMklnvKGnGMwMM8sjl7XNx7PmjMaHWHJyeWbnTT4EWAo1RrXu/wP93q2/vrVDkeM/qKIm47FisJAZNTkB2omz/HejIXanziOXrMHwWY/e3WzA/zBes/Sn6AvlStkmyu6k0W6xqccom5vBWs9z0z1Vmv6hkp2sIPHZU1Jp8y50WaOYXP9iFl3Zza/4sNyA1X1VZsS5IKH3/CHrEoWOMZTed5xboSwbJFH4m2Q==",

                "https://xcoic.com/d/l46tw3nx1jxf",
                "https://xcoic.com/d/zy9olm43yxlb",
                "https://xcoic.com/d/jczr40fxf6h8",
                "https://xcoic.com/d/menw4knk3jgq",
                "https://xcoic.com/e/fewc14kx4g5w",
                "https://embed69.org/f/tt4052886-5x06",
                "https://embed69.org/f/tt4052886-5x07",
                "https://embed69.org/f/tt4052886-5x08",
                "https://embed69.org/f/tt4052886-5x09",
                "https://embed69.org/f/tt4052886-5x10",
                "https://embed69.org/f/tt4052886-5x11",
                "https://embed69.org/f/tt4052886-5x12",
                "https://embed69.org/f/tt4052886-5x13",
                "https://embed69.org/f/tt4052886-5x14",
                "https://embed69.org/f/tt4052886-5x15",
                "https://embed69.org/f/tt4052886-5x16",

                "https://embed69.org/f/tt4052886-6x01",
                "https://embed69.org/f/tt4052886-6x02",
                "https://embed69.org/f/tt4052886-6x03",
                "https://embed69.org/f/tt4052886-6x04",
                "https://embed69.org/f/tt4052886-6x05",
                "https://embed69.org/f/tt4052886-6x06",
                "https://embed69.org/f/tt4052886-6x07",
                "https://embed69.org/f/tt4052886-6x08",
                "https://embed69.org/f/tt4052886-6x09",
                "https://embed69.org/f/tt4052886-6x10"

        };

        // Adapter del Spinner
        videosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoNames);
        videosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideos.setAdapter(videosAdapter);

        // Random para el botón aleatorio
        random = new Random();

        // Selección del Spinner
        spinnerVideos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0) {
                    String selectedVideoUrl = videoUrls[position];
                    if (selectedVideoUrl != null && !selectedVideoUrl.isEmpty()) {
                        openWatchActivity(selectedVideoUrl);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Nada
            }
        });

        // Botón aleatorio
        playRandomButton.setOnClickListener(v -> {
            if (videoUrls.length > 1) {
                int randomIndex = random.nextInt(videoUrls.length - 1) + 1; // evita el índice 0
                String randomVideoUrl = videoUrls[randomIndex];
                if (randomVideoUrl != null && !randomVideoUrl.isEmpty()) {
                    openWatchActivity(randomVideoUrl);
                }
            }
        });
    }

    private void openWatchActivity(String videoUrl) {
        Intent intent = new Intent(series3.this, WebViewActivityGeneral.class);
        intent.putExtra("url", videoUrl); // clave que usa WebViewActivityGeneral
        startActivity(intent);
    }
}
