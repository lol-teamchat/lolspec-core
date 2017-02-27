// ClrInjectionLib.h
#include <Windows.h>
#include <iostream>
#include <TlHelp32.h>
#include <stdlib.h>
#pragma once

using namespace System;

namespace ClrInjectionLib {

	public ref class Injector
	{
	public:
	    static bool Inject32(int processId, System::String^ dllName);
		static bool Inject(int processId, System::String^ dllName);
	};
}
