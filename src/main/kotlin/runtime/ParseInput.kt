package runtime


class ParseInput(val text: String, val offset: Int = 0) {
    val start: Int = 0

    fun matchChar(c: Char): ParseOutput {
        return matchCharIf { it == c }
    }

    fun matchCharIf(predicate: (Char) -> Boolean): ParseOutput {
        return when (val c = text.getOrNull(offset)) {
            null -> ParseFailure(this)
            else -> when (predicate(c)) {
                true -> ParseSuccess(ParseInput(text, offset + 1))
                false -> ParseFailure(this)
            }
        }
    }

    fun matchCharUntil(predicate: (Char) -> Boolean): ParseOutput {
        return matchCharIf { !predicate(it) }
    }

    fun matchCharAny(): ParseOutput {
        return matchCharIf { true }
    }


    fun matchString(s: String, insensive: Boolean = false): ParseOutput {
        val substring = text.substring(offset, offset + s.length)
        return when {
            substring.equals(s, ignoreCase = insensive) -> ParseSuccess(ParseInput(text, offset + s.length))
            else -> ParseFailure(this)
        }
    }

    fun matchRegex(regex: Regex): ParseOutput {
        return when (val match = regex.find(text, offset)) {
            null -> ParseFailure(this)
            else -> ParseSuccess(ParseInput(text, match.range.last + 1))
        }
    }


    fun matchOptional(parser: (ParseInput) -> ParseOutput): ParseOutput {
        return when (val output = parser(ParseInput(text, offset))) {
            is ParseSuccess -> output
            is ParseFailure -> ParseSuccess(this)
            else -> {
                throw Exception("Unexpected output: $output")
            }
        }
    }

    fun matchRepeats(parser: (ParseInput) -> ParseOutput, times: Int): ParseOutput {
        var output: ParseOutput = ParseSuccess(this)
        for (i in 1..times) {
            output = parser(ParseInput(text, offset))
            when (output) {
                is ParseSuccess -> continue
                is ParseFailure -> return ParseFailure(this)
                else -> {
                    throw Exception("Unexpected output: $output")
                }
            }
        }
        return output
    }


}