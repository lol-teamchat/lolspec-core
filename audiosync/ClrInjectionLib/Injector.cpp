// This is the main DLL file.

#define _WIN32_LEAN_AND_MEAN
#include <Windows.h>
#include "injector.h"

bool ClrInjectionLib::Injector::Inject(int pId, System::String^ dllName) {
	auto dllNameNative = (char*)(void*)System::Runtime::InteropServices::Marshal::StringToHGlobalAnsi(dllName);
	HANDLE Proc;
	HMODULE hLib;
	char buf[50] = { 0 };
	LPVOID RemoteString, LoadLibAddy;

	if (!pId)
		return false;

	Proc = OpenProcess(PROCESS_ALL_ACCESS, FALSE, pId);
	if (!Proc)
	{
		//sprintf(buf, "OpenProcess() failed: %d", GetLastError());
		////MessageBox(NULL, buf, "Loader", MB_OK); 
		//printf(buf);
		return false;
	}

	LoadLibAddy = (LPVOID)GetProcAddress(GetModuleHandle(L"kernel32.dll"), "LoadLibraryA");

	// Allocate space in the process for our DLL 
	RemoteString = (LPVOID)VirtualAllocEx(Proc, NULL, strlen(dllNameNative), MEM_RESERVE | MEM_COMMIT, PAGE_READWRITE);

	// Write the string name of our DLL in the memory allocated 
	WriteProcessMemory(Proc, (LPVOID)RemoteString, dllNameNative, strlen(dllNameNative), NULL);

	// Load our DLL 
	CreateRemoteThread(Proc, NULL, NULL, (LPTHREAD_START_ROUTINE)LoadLibAddy, (LPVOID)RemoteString, NULL, NULL);

	CloseHandle(Proc);
	return true;
}
bool ClrInjectionLib::Injector::Inject32(int processId, System::String^ dllName)
{
    auto process = OpenProcess(PROCESS_ALL_ACCESS, false, processId);

    if(process == nullptr)
    {
        auto err = GetLastError();
        DebugBreak();
        return false;
    }

    auto kernelHandle = GetModuleHandleA("Kernel32");

    auto dllNameNative = (char*)(void*)System::Runtime::InteropServices::Marshal::StringToHGlobalAnsi(dllName);

    auto remoteMemory = VirtualAllocEx(process, nullptr, dllName->Length, MEM_COMMIT, PAGE_READWRITE);
    if(!WriteProcessMemory(process, remoteMemory, (void*)dllNameNative, dllName->Length, nullptr))
    {
        auto err = GetLastError();
        DebugBreak();
        return false;
    }

    auto thread = CreateRemoteThread(process, nullptr, 0, (LPTHREAD_START_ROUTINE) GetProcAddress(kernelHandle, "LoadLibraryA"), remoteMemory, 0, nullptr);
    if(thread == nullptr)
    {
        auto err = GetLastError();
        DebugBreak();
        return false;
    }
    
    WaitForSingleObject(thread, INFINITE);

    DWORD exitCode;
    GetExitCodeThread(thread, &exitCode);

    CloseHandle(thread);
    VirtualFreeEx(process, remoteMemory, dllName->Length, MEM_RELEASE);

    System::Runtime::InteropServices::Marshal::FreeHGlobal((IntPtr)dllNameNative);

    return true;
}
 