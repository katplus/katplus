/**
 * @author kraity
 * @since 0.0.6
 */
open module plus.kat.core {
    exports plus.kat;
    exports plus.kat.chain;

    exports plus.kat.spare;
    exports plus.kat.entity;

    exports plus.kat.solver;
    exports plus.kat.stream;

    requires static java.desktop;
    requires transitive plus.kat.actor;
}
