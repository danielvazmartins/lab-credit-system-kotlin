package br.com.danielvazmartins.labcreditsystemkotlin.exception

class BusinessException(override val message: String?): RuntimeException(message) {
}