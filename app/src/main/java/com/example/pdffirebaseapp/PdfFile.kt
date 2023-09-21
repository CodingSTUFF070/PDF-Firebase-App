package com.example.pdffirebaseapp

data class PdfFile(val fileName : String , val downloadUrl : String){
    constructor() : this("","")
}
