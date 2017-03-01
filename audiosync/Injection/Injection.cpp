// 32-bit injection unmanaged DLL injection

#include <iostream>
#include <direct.h>
#include <windows.h>
#include <tlhelp32.h>
#include "Injection.h"
#include "stdafx.h"

using namespace System::Runtime::InteropServices;
using namespace Injection;

// need to explicitly define constructor otherwise there is a link error
Injector::Injector() {

}

LPCTSTR Injector::SzToLPCTSTR(char* szString) {
	LPTSTR lpszRet;
	size_t size = strlen(szString)+1;

	lpszRet = (LPTSTR)malloc(MAX_PATH);
	mbstowcs_s(NULL, lpszRet, size, szString, _TRUNCATE);

	return lpszRet;
}

BOOL Injector::InjectDll(DWORD dwPid, char* szDllPath) {
	DWORD	dwMemSize;
	HANDLE	hProc;
	LPVOID	lpRemoteMem, lpLoadLibrary;
	BOOL	bRet = FALSE;

	if ((hProc = OpenProcess(PROCESS_VM_OPERATION | PROCESS_VM_WRITE | PROCESS_CREATE_THREAD, FALSE, dwPid)) != NULL)
	{
		// potential loss of data -> need to rework this area
		dwMemSize = strlen(szDllPath) + 1;
		if ((lpRemoteMem = VirtualAllocEx(hProc, NULL, dwMemSize, MEM_COMMIT, PAGE_READWRITE)) != NULL)
			if (WriteProcessMemory(hProc, lpRemoteMem, (LPCVOID)szDllPath, dwMemSize, NULL))
			{
				lpLoadLibrary = GetProcAddress(GetModuleHandleA("Kernel32.dll"), "LoadLibraryA");
				if (CreateRemoteThread(hProc, NULL, 0, (LPTHREAD_START_ROUTINE)lpLoadLibrary, lpRemoteMem, 0, NULL) != NULL)
					bRet = TRUE;
			}
	}
	CloseHandle(hProc);

	return bRet;
}

// CLR Wrapper inject
BOOL Injector::Inject(int pid, System::String^ dllPath) {
	
	char * szDllPath;
	szDllPath = (char*)(void*)Marshal::StringToHGlobalAnsi(dllPath);
// Debug message to make sure DLL path is correct
#ifdef _DEBUG
	MessageBox(
		0,
		SzToLPCTSTR(szDllPath),
		L"dll path",
		MB_DEFBUTTON1
	);
	MessageBox(
		0,
		SzToLPCTSTR((char *)(void*)Marshal::StringToHGlobalAnsi(pid.ToString())),
		L"league of legends.exe pid",
		MB_DEFBUTTON1
	);
#endif
	
	DWORD dwPid = static_cast<DWORD>(pid);
	return InjectDll(dwPid, szDllPath);


}