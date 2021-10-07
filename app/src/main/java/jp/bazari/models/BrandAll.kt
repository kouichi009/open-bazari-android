package jp.bazari.models

//class BrandAll(/*var enName: String, var jpName: String, var jpHira: String*/) {
//    var enName: String = ""
//    var jpName: String = ""
//    var jpHira: String = ""
//
//
//}


class BrandAll {

    var enName: String = ""
    var jpName: String = ""
    var jpHira: String = ""

    constructor() {

    }

    constructor(enName: String, jpName: String, jpHira: String) {
        this.enName = enName
        this.jpName = jpName
        this.jpHira = jpHira
    }


}