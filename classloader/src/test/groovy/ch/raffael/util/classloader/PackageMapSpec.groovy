package ch.raffael.util.classloader;


import spock.lang.Specification

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
class PackageMapSpec extends Specification {

    private PackageMap<String> map = new PackageMap<String>()

    def "Exact match overrides sub-package match"() {
      given:
        put("foo/bar", "foo/bar/")

      expect:
        map.get("foo/bar") == "foo/bar"
    }

    def "Return direct recursive match if no direct match was found"() {
      given:
        put("foo/bar/")

      expect:
        map.get("foo/bar") == "foo/bar/"
    }

    def "Search upwards for recursive matches"() {
      given:
        put("foo/")

      expect:
        map.get("foo/bar/foobar") == "foo/"
    }

    def "Deeper recursive entries override higher ones"() {
      given:
        put("foo/", "foo/bar/")

      expect:
        map.get("foo/bar/foobar") == "foo/bar/"
    }

    def "Upwards search doesn't match non-recursive entries"() {
      given:
        put("foo")

      expect:
        map.get("foo/bar/foobar") == null
    }

    private void put(String... names) {
        names.each {
            map.put(it, it)
        }
    }

}
