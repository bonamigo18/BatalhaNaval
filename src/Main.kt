import kotlin.random.Random


const val BOARD_SIZE = 10
const val TENTATIVAS = 15


const val AGUA = '~'
const val ERRO = 'M'
const val PORTA_AVIAO = 'P'
const val CRUZADOR = 'C'
const val REBOCADOR = 'R'



// enum para dar tipos aos navios
enum class Navios(val simbolo: Char, val pontos: Int, val tamanho: Int) {
    PORTA_AVIAO('A', 5, 1),
    CRUZADOR('C', 15, 1),
    REBOCADOR('R', 10, 1)
}

class BatalhaNavalJogo {
    val tabuleiro = Array(BOARD_SIZE) { CharArray(BOARD_SIZE) { AGUA } }
    val navios = mutableListOf<Pair<Navios, Pair<Int, Int>>>()
    val tiros = mutableSetOf<Pair<Int, Int>>()
    var pontos = 0

    init {
        montarTabuleiro()
    }

    fun montarTabuleiro() {
        ancorarNavios(Navios.PORTA_AVIAO, 10)
        ancorarNavios(Navios.CRUZADOR, 1)
        ancorarNavios(Navios.REBOCADOR, 2)
    }

    fun ancorarNavios(ship: Navios, quantidade: Int) {
        repeat(quantidade) {
            var ancorar = false
            while (!ancorar) {
                val linha = Random.nextInt(BOARD_SIZE)
                val coluna = Random.nextInt(BOARD_SIZE)
                if (tabuleiro[linha][coluna] == AGUA) {
                    navios.add(Pair(ship, Pair(linha, coluna)))
                    ancorar = true
                }
            }
        }
    }

    fun jogar() {
        repeat(TENTATIVAS) { tentativa ->
            println("\nTentativa ${tentativa + 1}/$TENTATIVAS")
            mostrarTabuleiro(showShips = false)

            // Solicita a linha e coluna separadamente
            val row = getCoordenada("linha")
            val col = getCoordenada("coluna")

            // Verifica se o jogador já atirou na mesma posição
            if (tiros.contains(Pair(row, col))) {
                println("Você já atirou aqui! Tente outras coordenadas.")
            } else {
                tiros.add(Pair(row, col))
                checarTiro(row, col)
            }
        }

        gameOver()
    }

    fun getCoordenada(type: String): Int {
        while (true) {
            println("Informe a $type (0 a ${BOARD_SIZE - 1}):")
            val input = readLine()?.toIntOrNull()
            if (input != null && input in 0 until BOARD_SIZE) {
                return input
            } else {
                println("Entrada inválida. Digite um número entre 0 e ${BOARD_SIZE - 1}.")
            }
        }
    }

    fun checarTiro(row: Int, col: Int) {
        val hitShip = navios.find { it.second == Pair(row, col) }
        if (hitShip != null) {
            tabuleiro[row][col] = when (hitShip.first) {
                Navios.PORTA_AVIAO -> PORTA_AVIAO
                Navios.CRUZADOR -> CRUZADOR
                Navios.REBOCADOR -> REBOCADOR
            }
            pontos += hitShip.first.pontos
            println("Acertou um ${hitShip.first.name}! Pontos ganhos: ${hitShip.first.pontos}")
        } else {
            val missRadius = calcularRaioNavio(row, col)
            tabuleiro[row][col] = when (missRadius) {
                1 -> '1'
                2 -> '2'
                3 -> '3'
                else -> ERRO
            }
            println("Tiro na água. Distância para o alvo mais próximo: $missRadius casas.")
        }
    }

    fun calcularRaioNavio(row: Int, col: Int): Int {
        for (radius in 1..3) {
            for (i in -radius..radius) {
                if (navioProximo(row + i, col)) return radius
                if (navioProximo(row, col + i)) return radius
            }
        }
        return 4 // Indica um erro maior
    }

    fun navioProximo(row: Int, col: Int): Boolean {
        return navios.any { it.second == Pair(row, col) }
    }

    fun mostrarTabuleiro(showShips: Boolean) {
        println("  " + (0 until BOARD_SIZE).joinToString(" "))
        for (i in tabuleiro.indices) {
            print("$i ")
            for (j in tabuleiro[i].indices) {
                val cell = tabuleiro[i][j]
                if (!showShips && cell in listOf(PORTA_AVIAO, CRUZADOR, REBOCADOR, ERRO, '1', '2', '3')) {
                    print("$cell ")
                } else if (showShips || cell != AGUA) {
                    print("$cell ")
                } else {
                    print("$AGUA ")
                }
            }
            println()
        }
    }

    fun gameOver() {
        println("\nFim de jogo! Pontuação final: $pontos pontos.")
        mostrarTabuleiro(showShips = true)
        println("Deseja jogar novamente? (S/N)")
        val response = readLine()?.uppercase()
        if (response == "S") {
            resetar()
            jogar()
        } else {
            println("Obrigado por jogar!")
        }
    }

    fun resetar() {
        navios.clear()
        tiros.clear()
        pontos = 0
        for (i in tabuleiro.indices) {
            tabuleiro[i].fill(AGUA)
        }
        montarTabuleiro()
    }

}
fun main() {
    val game = BatalhaNavalJogo()
    game.jogar()
}
