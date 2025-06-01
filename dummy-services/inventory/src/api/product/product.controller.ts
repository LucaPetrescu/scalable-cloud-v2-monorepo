import {
  Controller,
  Post,
  Request,
  Logger,
  Get,
  Query,
  Res,
  HttpStatus,
  HttpException,
} from '@nestjs/common';
import { ProductService } from './product.service';
import { HttpMetricsService } from '../metrics/http-metrics.service';

@Controller('product')
export class ProductController {
  logger: Logger;

  constructor(
    private readonly productService: ProductService,
    private readonly httpMetricsService: HttpMetricsService,
  ) {
    this.logger = new Logger('ProductController');
  }

  @Post('create')
  async create(@Request() req, @Res() res): Promise<any> {
    const newProduct = req.body;

    const starTime = Date.now();
    const durationInSeconds = (Date.now() - starTime) / 1000;
    const { method, path: route } = req;

    try {
      this.httpMetricsService.incrementRequestCounter(
        method,
        route,
        200,
        durationInSeconds,
      );

      const query = {
        name: newProduct.productName,
        barCode: newProduct.barCode,
      };

      const isProduct = await this.productService.findOne(query);

      if (isProduct) {
        throw new HttpException(
          'Product already Exists. Check the name or the barcode',
          HttpStatus.CONFLICT,
        );
      }

      const product = this.productService.create(newProduct);
      res.status(HttpStatus.CREATED).send(product);
      return product;
    } catch (err) {
      this.logger.error('Something went wrong. Please try again later:', err);
      res.status(HttpStatus.INTERNAL_SERVER_ERROR).send(err);
    }
  }

  @Get('getProduct')
  async getProduct(
    @Request() req,
    @Query('productId') productId: string,
    @Res() res,
  ): Promise<any> {
    const startTime = Date.now();
    const durationInSeconds = (Date.now() - startTime) / 1000;
    const { method, path: route } = req;

    try {
      this.httpMetricsService.incrementRequestCounter(
        method,
        route,
        200,
        durationInSeconds,
      );

      const query = { productId: productId };
      const product = await this.productService.findOne(query);

      if (!product) {
        throw new HttpException('Product does not exist', HttpStatus.NOT_FOUND);
      }

      res.status(HttpStatus.OK).send(product);
      return product;
    } catch (err) {
      this.logger.error('Something went wrong. Please try again: ', err);
      res.status(HttpStatus.INTERNAL_SERVER_ERROR).send(err);
    }
  }

  @Get('getProductInventory')
  async getProductInventory(
    @Request() req,
    @Query('productId') productId: string,
    @Res() res,
  ): Promise<any> {
    const startTime = Date.now();
    const durationInSeconds = (Date.now() - startTime) / 1000;
    const { method, path: route } = req;

    try {
      this.httpMetricsService.incrementRequestCounter(
        method,
        route,
        200,
        durationInSeconds,
      );

      const query = { productId: productId };
      const product = await this.productService.findOne(query);

      if (!product) {
        throw new HttpException('Product does not exist', HttpStatus.NOT_FOUND);
      }

      const productInventory = product.quantity;

      res.status(HttpStatus.OK).send({ quantity: productInventory });

      return { quantity: productInventory };
    } catch (err) {
      this.logger.error('Something went wrong. Please try again: ', err);
      res.status(HttpStatus.INTERNAL_SERVER_ERROR).send(err);
    }
  }

  @Get('getAllProducts')
  async getAllProducts(@Request() req, @Res() res): Promise<any> {
    const startTime = Date.now();
    const durationInSeconds = (Date.now() - startTime) / 1000;
    const { method, path: route } = req;

    try {
      this.httpMetricsService.incrementRequestCounter(
        method,
        route,
        200,
        durationInSeconds,
      );

      const products = await this.productService.findAll();

      res.status(HttpStatus.OK).send(products);
      return products;
    } catch (err) {
      this.logger.error('Something went wrong. Please try again: ', err);
      res.status(HttpStatus.INTERNAL_SERVER_ERROR).send(err);
    }
  }
}
