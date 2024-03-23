/**
 * @author kraity
 * @since 0.0.6
 */
open module plus.kat {
    exports plus.kat;
    exports plus.kat.core;

    exports plus.kat.lang;
    exports plus.kat.flow;

    exports plus.kat.chain;
    exports plus.kat.spare;

    requires static java.desktop;
    requires transitive plus.kat.actor;
}
