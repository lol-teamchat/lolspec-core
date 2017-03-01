// Injection.h

#pragma once

using namespace System;

// entry point for CLR to unmanaged code
namespace Injection {

	// Injector class for main injection procedure
	public ref class Injector {
		public:
			Injector();
			BOOL Inject(System::String^ processName, System::String^ dllName);
		private: 
			BOOL InjectDll(DWORD dwPid, char * szDllPath);
			char * GetCurrentDir();
			LPCTSTR SzToLPCTSTR(char * szString);
			void WaitForProcessToAppear(LPCTSTR lpczProc, DWORD dwDelay);
			DWORD GetProcessIdByName(LPCTSTR lpczProc);
	};
	
}