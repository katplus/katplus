-keep, allowoptimization, allowobfuscation
  @plus.kat.actor.Magus class *
-keepclassmembers, allowobfuscation class * {
  @plus.kat.actor.Magic <fields>;
  @plus.kat.actor.Magic <methods>;
}

-dontwarn java.beans.Transient

-if class * {
  @plus.kat.actor.Magic <fields>;
}
-keep, allowobfuscation, allowoptimization class <1>
-if class * {
  @plus.kat.actor.Magic <methods>;
}
-keep, allowobfuscation, allowoptimization class <1>

-keep, allowobfuscation class plus.kat.Klass
-keep, allowobfuscation class * extends plus.kat.Klass
