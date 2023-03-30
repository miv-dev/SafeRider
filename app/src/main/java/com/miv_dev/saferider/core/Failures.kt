package com.miv_dev.saferider.core

sealed class Error(msg: String): Throwable(msg)

class ScanError(msg: String) : Error(msg)
class PermissionError(msg: String): Error(msg)
