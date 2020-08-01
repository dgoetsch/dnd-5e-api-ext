package components

import kotlinx.html.A
import kotlinx.html.DIV
import react.RBuilder
import react.dom.RDOMBuilder
import react.dom.a
import react.dom.div

typealias Builder<T> = RDOMBuilder<T>.() ->Unit
fun RBuilder.card(
        titleTag: Builder<DIV>? = null,
        cardTextTag: Builder<DIV>? = null,
        cardBodyContent: Builder<DIV>? = null,
        subTitleTag: Builder<DIV>? = null,
        cardLinks: Map<String, Builder<A>> = emptyMap(),
        cardHeader: Builder<DIV>? = null,
        cardFooter: Builder<DIV>? = null
) {
    div("card") {
        cardHeader?.let { div("card-header", it)}
        div("card-body") {
            titleTag?.let { div("card-title", it) }
            subTitleTag?.let { div("card-subtitle", it)}
            cardTextTag?.let { div("card-text", it) }
            cardBodyContent?.invoke(this)
            cardLinks.forEach { (href, tagBuilder) ->
                a(href = href, classes = "card-link", block = tagBuilder)
            }
        }
        cardFooter?.let { div("card-footer text-muted", it)}
    }
}