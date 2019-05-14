#include <stdio.h>
#include <stdlib.h>
#include <string.h>

extern int __init();

int main() {
    return __init();
}

typedef (long) int64_t;
typedef (char *) handle_t;

/*
	builtin_print;
	builtin_println;
	builtin_getString;
	builtin_getInt;
	builtin_toString;
	builtin_string_length;
	builtin_string_substring;
	builtin_string_parseInt;
	builtin_string_ord;
	builtin_stringConcat;
	builtin_stringCompare
*/

void __print(handle_t str) {
	printf("%s", str + 8);
}

void __println(handle_t str) {
	puts(str + 8);
}

handle_t  __getString() {
	static char __buffer[1024 * 1024];	//	1MB buffer
	scanf("%s", __buffer);
	int length = strlen(__buffer);
	handle_t ret = malloc(length + 8);
	*((int64_t *) ret) = length;
	strcpy(ret + 8, __buffer);
	return ret;
}

int64_t __getInt() {
	int64_t ret;
	scanf("%ld", &ret);
	return ret;
}

handle_t __toString(int64_t a) {
	handle_t ret = malloc(8 + 24);
	*((int64_t *) ret) = sprintf(ret + 8, "%ld", a);
	return ret;
}

int64_t __string_length(handle_t ptr) {
	return *((int64_t *) ptr);
}

pointer_t __string_substring(handle_t ptr, int left, int right) {
	int length = right - left + 1;
	handle_t ret = malloc(8 + length + 1);
	*((int64_t *) ret) = length;
	int i;
	for (i = 0; i < length; ++i) {
		ret[8 + i] = ptr[8 + left + i];
	}
	ret[8 + length] = 0;
	return ret;
}

int64_t __string_parseInt(handle_t ptr) {
	int64_t value = 0;
	int neg = 0;
	ptr += 8;
	if (*ptr == '-') {
		neg = 1;
		ptr++;
	}
	while ('0' <= *ptr && *ptr <= '9') {
		value = value * 10 + (*ptr - '0');
		++ptr;
	}
	return neg ? -value : value;
}

int64_t __string_ord(handle_t ptr, int64_t pos) {
	return ptr[8 + pos];
}

handle_t __stringConcat(handle_t sa, handle_t sb) {
	int64_t la = *((int64_t *) sa);
	int64_t lb = *((int64_t *) sb);
	handle_t ret = malloc(la + lb + 1 + 8);
	*((int64_t *) ret) = la + lb;
	int i;
	for (i = 0; i < la; ++i) {
		ret[8 + i] = sa[8 + i];
	}
	for (i = 0; i < lb; ++i) {
		ret[8 + la + i] = sb[8 + i];
	}
	ret[8 + la + lb] = '\0';
	return ret;
}

int64_t __stringCompare(handle_t sa, handle_t sb) {
	return strcmp(sa + 8, sb + 8);
}