// 32-bit injection unmanaged DLL injection

#include <iostream>
#include <direct.h>
#include <windows.h>
#include <tlhelp32.h>
#include "Injection.h"
#include "stdafx.h"

using namespace Injection;

// need to explicitly define constructor otherwise there is a link error
Injector::Injector() {

}

char* Injector::GetCurrentDir() {
	char* szRet = (char*)malloc(MAX_PATH);
	_getcwd(szRet, MAX_PATH);
	return szRet;
}

LPCTSTR Injector::SzToLPCTSTR(char* szString) {
	LPTSTR lpszRet;
	size_t size = strlen(szString)+1;

	lpszRet = (LPTSTR)malloc(MAX_PATH);
	mbstowcs_s(NULL, lpszRet, size, szString, _TRUNCATE);

	return lpszRet;
}

void Injector::WaitForProcessToAppear(LPCTSTR lpczProc, DWORD dwDelay) {
	HANDLE			hSnap;
	PROCESSENTRY32	peProc;
	BOOL			bAppeared = FALSE;

	while (!bAppeared)
	{
		if ((hSnap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0)) != INVALID_HANDLE_VALUE)
		{
			peProc.dwSize = sizeof(PROCESSENTRY32);
			if (Process32First(hSnap, &peProc))
				while (Process32Next(hSnap, &peProc) && !bAppeared)
					if (!lstrcmp(lpczProc, peProc.szExeFile))
						bAppeared = TRUE;
		}
		CloseHandle(hSnap);
		Sleep(dwDelay);
	}
	//std::cout << "process appeared" << std::endl;
}

DWORD Injector::GetProcessIdByName(LPCTSTR lpczProc) {
	HANDLE			hSnap;
	PROCESSENTRY32	peProc;
	DWORD			dwRet = -1;

	if ((hSnap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0)) != INVALID_HANDLE_VALUE)
	{
		peProc.dwSize = sizeof(PROCESSENTRY32);
		if (Process32First(hSnap, &peProc))
			while (Process32Next(hSnap, &peProc))
				if (!lstrcmp(lpczProc, peProc.szExeFile))
					dwRet = peProc.th32ProcessID;
	}
	CloseHandle(hSnap);

	return dwRet;
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

// TODO
BOOL Injector::Inject(System::String^ processName, System::String^ dllName) {
	

	// maybe change first argument to uint for processId then call
	// another function to inject

	// convert processName to LPCTSTR
	// get current path and concatenate the dllName to it
	// convert dllPath to char*
	// call InjectDll


	// TODO
	return false;

}

//int main(void) {
//	char szProc[MAX_PATH];
//	char szDll[MAX_PATH];
//
//	char*   szDllPath;
//	LPTSTR	lpszProc = NULL;
//
//	char lolstr[] = "League of Legends.exe";
//	char dllstr[] = "LeagueReplayHook.dll";
//	strcpy_s(szProc, lolstr);
//	strcpy_s(szDll, dllstr);
//
//	cout << "process name: " << szProc << endl;
//	cout << "dll name: " << dllstr << endl;
//
//	szDllPath = GetCurrentDir();
//	strcat_s(szDllPath, MAX_PATH, "\\");
//	strcat_s(szDllPath, MAX_PATH, szDll);
//
//	cout << "waiting for league of legends to start" << endl;
//
//	WaitForProcessToAppear(SzToLPCTSTR(szProc), 100);
//
//	if (InjectDll(GetProcessIdByName(SzToLPCTSTR(szProc)), szDllPath))
//		cout << "success" << endl;
//	else
//		cout << "fail" << endl;
//	cout << "\n";
//
//	free(szDllPath);
//
//
//	return EXIT_SUCCESS;
//}
