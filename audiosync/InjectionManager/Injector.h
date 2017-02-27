#pragma once

using namespace System;

namespace InjectionManager {
	public ref class Injector {
		public:
			static bool Inject(int processId, System::String^ dllName);
	};
}
