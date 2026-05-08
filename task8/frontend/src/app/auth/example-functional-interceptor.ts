import { HttpInterceptorFn } from '@angular/common/http';

export const exampleFunctionalInterceptor: HttpInterceptorFn = (req, next) => {
  console.log("example-functional-interceptor");
  return next(req);
};


