// Injection.h

#pragma once

using namespace System;

// entry point for CLR to unmanaged code
namespace Injection {

	// Injector class for main injection procedure
	public ref class Injector {
		public:
			Injector();
			BOOL Inject(int pid, System::String^ dllPath);
		private: 
			BOOL InjectDll(DWORD dwPid, char * szDllPath);
			LPCTSTR SzToLPCTSTR(char * szString);
	};
	
}